package org.spooler.document;

/**
 * Письмо
 */
public class LetterPrintDocument extends AbstractPrintDocument {

    public LetterPrintDocument() {
        super("LetterDocument", PaperSize.A5, 4000);
    }
}
