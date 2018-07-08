package org.spooler.document;

/**
 * Изображение
 */
public class ImagePrintDocument extends AbstractPrintDocument {
    public ImagePrintDocument()
    {
        super("ImageDocument", PaperSize.A3, 12000);
    }
}
