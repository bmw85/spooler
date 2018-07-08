package org.spooler.document;

/**
 * Схема
 */
public class SchemePrintDocument extends AbstractPrintDocument {
    public SchemePrintDocument() {
        super("SchemeDocument", PaperSize.A2, 18000);
    }
}
