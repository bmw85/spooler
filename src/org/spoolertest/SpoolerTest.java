package org.spoolertest;

import org.spooler.PrintedSortOrder;
import org.spooler.Spooler;
import org.spooler.document.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Небольшая программа для демонстрации возможностей класса диспетчера печати Spooler
 */
public class SpoolerTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Spooler spooler = new Spooler();

        Scanner consoleScanner = new Scanner(System.in);
        while (true) {

            System.out.print("1. Add random documents to spooler queue" +
                    "\n2. Manual add document to spooler queue " +
                    "\n3. Show printed documents list " +
                    "\n4. Stop and print remaining documents" +
                    "\n5. Start " +
                    "\n6. Show average print time" +
                    "\n7. Remove document from queue" +
                    "\nPlease enter menu item number or Q to quit\n ");

            switch (consoleScanner.next().toLowerCase()) {
                case "q":
                    System.out.println("Exit!");
                    System.exit(0);
                case "1":
                    inputLoop:
                    while (true) {
                        System.out.print("Please, enter number of documents to add: ");
                        List<PrintDocument> randomDocuments;
                        String input = consoleScanner.next();
                        try {
                            randomDocuments = generateDocuments(Integer.parseInt(input));
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect number.");
                            continue inputLoop;
                        }

                        randomDocuments.forEach(new Consumer<PrintDocument>() {
                            @Override
                            public void accept(PrintDocument printDocument) {
                                spooler.addDocument(printDocument);
                            }
                        });
                        break;
                    }
                    break;
                case "2":
                    inputloop:
                    while (true) {
                        System.out.println("Enter type of document to add (I)mage, (L)etter, (S)cheme, (T)ext; (E)xit menu: ");
                        switch (consoleScanner.next().toLowerCase()) {
                            case "i":
                                spooler.addDocument(new ImagePrintDocument());
                                break;
                            case "l":
                                spooler.addDocument(new LetterPrintDocument());
                                break;
                            case "s":
                                spooler.addDocument(new SchemePrintDocument());
                                break;
                            case "t":
                                spooler.addDocument(new TextPrintDocument());
                                break;
                            case "e":
                                break inputloop;
                        }
                    }
                    break;
                case "3":
                    PrintedSortOrder printedSortOrder;
                    inputLoop:
                    while (true) {
                        System.out.print("Select sort order: (D)ocument Type, Print (T)ime, (P)aper Size ");
                        switch (consoleScanner.next().toLowerCase()) {
                            case "d":
                                printedSortOrder = PrintedSortOrder.DOCUMENT_TYPE;
                                break inputLoop;
                            case "t":
                                printedSortOrder = PrintedSortOrder.DOCUMENT_PRINTTIME;
                                break inputLoop;
                            case "p":
                                printedSortOrder = PrintedSortOrder.DOCUMENT_PAPERSIZE;
                                break inputLoop;
                        }
                    }

                    List<PrintDocument> printedDocuments = spooler.getPrintedDocuments(printedSortOrder);
                    if (printedDocuments.size() == 0) {
                        System.out.println("There is no printed documents");
                        break;
                    }
                    showPrintDocuments(printedDocuments);
                    break;
                case "4": //TODO вывести список
                    showPrintDocuments(spooler.stopPrint());
                    break;
                case "5":
                    spooler.startPrint();
                    break;
                case "6":
                    System.out.printf("Avarage print time(ms): %d%n", spooler.getAveragePrintTime());
                    break;
                case "7":
                    List<PrintDocument> queuedDocumentsList = spooler.getQueuedDocuments();
                    showPrintDocuments(queuedDocumentsList);

                    inputLoop:
                    while (true) {
                        System.out.println("Enter number of the document you want to delete, L to list queue or E to exit");
                        String input = consoleScanner.next().toLowerCase();
                        int indexOfDocumentToDelete = 0;

                        switch (input) {
                            case "e":
                                break inputLoop;
                            case "l":
                                queuedDocumentsList = spooler.getQueuedDocuments();
                                showPrintDocuments(queuedDocumentsList);
                            default:
                                try {
                                    indexOfDocumentToDelete = Integer.parseInt(input);
                                } catch (NumberFormatException e) {
                                    System.out.println("Wrong number format");
                                    continue inputLoop;
                                }

                                if (indexOfDocumentToDelete <= 0 || indexOfDocumentToDelete > queuedDocumentsList.size() + 1) {
                                    System.out.println("Index out of bounds");
                                    continue inputLoop;
                                }

                                Future<Boolean> future = spooler.removeDocument(queuedDocumentsList.get(indexOfDocumentToDelete - 1));

                                if (future.get()) {
                                    System.out.println("Document successfully removed from queue:");
                                } else {
                                    System.out.println("Can not remove document, it is probably alredy printed:");
                                }
                                showPrintDocument(indexOfDocumentToDelete, queuedDocumentsList.get(indexOfDocumentToDelete - 1));
                        }

                    }
                    break;
            }

            System.out.println("=========================\n");
        }

    }

    /**
     * Создает случайный список PrintDocument
     *
     * @param number Количество элементов которые нужно сгенерировать
     * @return случайный список PrintDocument
     */
    private static List<PrintDocument> generateDocuments(int number) {
        ArrayList<PrintDocument> result = new ArrayList<PrintDocument>();
        for (int i = 0; i < number; i++) {
            result.add(getRandomPrinteryDocument());
        }
        return result;
    }

    /**
     * Возвращает один случайный объект из наследников {@link AbstractPrintDocument}
     *
     * @return
     */
    private static PrintDocument getRandomPrinteryDocument() {
        //return new PrintDocument();
        PrintDocument printDocument;
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0:
                printDocument = new ImagePrintDocument();
                break;
            case 1:
                printDocument = new LetterPrintDocument();
                break;
            case 2:
                printDocument = new SchemePrintDocument();
                break;
            case 3:
                printDocument = new TextPrintDocument();
            default:
                printDocument = new TextPrintDocument();
        }

        return printDocument;

    }

    /**
     * Выводит на консоль информацию о документах
     *
     * @param printDocuments список документов
     */
    private static void showPrintDocuments(List<PrintDocument> printDocuments) {
        System.out.printf("%-5s %-16s %-11s %-10s%n", "#", "Document Type", "Paper Size", "Print Time");
        long k = 0;
        for (PrintDocument printDocument : printDocuments) {
            showPrintDocument(++k, printDocument);
        }
    }

    private static void showPrintDocument(long index, PrintDocument printDocument) {
        System.out.printf("%-5d %-16s %-11s %-10d%n", index, printDocument.getDocumentTypeName(), printDocument.getPaperSize().toString(), printDocument.getPrintTime());
    }
}
