package org.spooler;


import org.spooler.document.PrintDocument;

import java.util.*;
import java.util.concurrent.*;

/**
 * Класс диспетчера очереди печати
 */
public class Spooler {
    private final Queue<PrintDocument> documentsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<PrintDocument> printedDocuments = new ConcurrentLinkedDeque<>();
    private final ExecutorService spoolerExecutor = Executors.newFixedThreadPool(8);
    //счетчик-барьер для ожидания на случай отсутствия документов в очереди
    private final CountDownLatch noDocumentsInQueueWaitCountDown = new CountDownLatch(1);
    private final CountDownLatch printStoppedCountDown = new CountDownLatch(1);
    private boolean isStopped = false;


    /**
     * В конструкторе запускается основной цикл диспетчера печати, отправляет документы на печать, сохраняет список напечатанных элементов, если
     * очередь документов на печать пуста - ждет поступления новых
     *
     * @throws InterruptedException
     */
    public Spooler() {

        spoolerExecutor.submit(new Callable<Object>() {
            @Override
            public Object call() throws InterruptedException {
                while (true) {
                    if (isStopped) {
                        printStoppedCountDown.await();
                    }

                    if (!documentsQueue.isEmpty()) {
                        PrintDocument printDocument = documentsQueue.poll();
                        PrintDocument(printDocument);
                        printedDocuments.add(printDocument);
                    } else {
                        //Очередь пуста, ждем пока поступят документы
                        noDocumentsInQueueWaitCountDown.await();
                    }
                }
            }
        });
    }


    /**
     * Эмуляция печати документа, ждет столько миллисекунд, сколько указано в свойствах документа
     *
     * @param printDocument документ для печати
     */
    private void PrintDocument(PrintDocument printDocument) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(printDocument.getPrintTime());
    }

    /**
     * Добавляет документ в очередь и сообщает об этом в главный цикл печати в случае если он стоит в ожидании
     */
    public Future<Boolean> addDocument(PrintDocument printDocument) {
        Future<Boolean> addFinish = spoolerExecutor.submit(
                new Callable() {
                    @Override
                    public Boolean call() {
                        Boolean result = documentsQueue.add(printDocument);
                        noDocumentsInQueueWaitCountDown.countDown();
                        return result;
                    }
                });
        return addFinish;
    }

    /**
     * Удаляет документ из очереди
     *
     * @param printDocument документ подлежащий удалению
     * @return true если удалось удалить документ, иначе false
     */
    public Future<Boolean> removeDocument(PrintDocument printDocument) {
        return spoolerExecutor.submit(
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return documentsQueue.remove(printDocument);
                    }
                });


    }

    /**
     * Останавливает диспетчер печати, отменяет печать документов в очереди
     *
     * @return Список документов оставшихся в очереди
     */
    public List<PrintDocument> stopPrint() {
        isStopped = true;
        return getQueuedDocuments();
    }

    public void startPrint() {
        isStopped = false;
        printStoppedCountDown.countDown();
    }


    public List<PrintDocument> getPrintedDocuments(final PrintedSortOrder printedSortOrder) {
        ArrayList<PrintDocument> result = new ArrayList<PrintDocument>(printedDocuments);
        Comparator<PrintDocument> comparator;

        switch (printedSortOrder) {
            case PRINT_ORDER:
                return result;
            case DOCUMENT_PAPERSIZE:
                comparator = new Comparator<PrintDocument>() {
                    @Override
                    public int compare(PrintDocument o1, PrintDocument o2) {
                        return o1.getPaperSize().compareTo(o2.getPaperSize());
                    }
                };
                break;
            case DOCUMENT_TYPE:
                comparator = new Comparator<PrintDocument>() {
                    @Override
                    public int compare(PrintDocument o1, PrintDocument o2) {
                        return o1.getDocumentTypeName().compareTo(o2.getDocumentTypeName());
                    }
                };
                break;
            case DOCUMENT_PRINTTIME:
                comparator = new Comparator<PrintDocument>() {
                    @Override
                    public int compare(PrintDocument o1, PrintDocument o2) {
                        return Integer.compare(o1.getPrintTime(), o2.getPrintTime());
                    }
                };
                break;
            default:
                return result;
        }
        Collections.sort(result, comparator);

        return result;
    }

    public List<PrintDocument> getQueuedDocuments() {
        return new ArrayList<PrintDocument>(documentsQueue);
    }

    public int getAveragePrintTime() {
        if (printedDocuments == null || printedDocuments.size() == 0)
            return 0;

        long sum = 0;
        for (PrintDocument printDocument : printedDocuments) {
            sum += printDocument.getPrintTime();
        }
        return (int) (sum / printedDocuments.size());
    }
}

