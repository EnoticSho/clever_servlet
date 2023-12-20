package clevertec.utils.pdfserializer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PdfSerializer {

    public Path serializeObjectToPdf(Object classObject) {
        String simpleName = classObject.getClass().getSimpleName();
        Document document = new Document();
        String pdfFilePath = createPdfFilePath(simpleName);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            addTitle(document, simpleName);
            addClassFieldsToPdf(writer, classObject);
        } catch (DocumentException | FileNotFoundException e) {
            log.error("Error while creating PDF: " + e.getMessage(), e);
            throw new RuntimeException("Error while creating PDF", e);
        } catch (IllegalAccessException e) {
            log.error("Illegal access: " + e.getMessage(), e);
            throw new RuntimeException("Error accessing fields", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return Path.of(pdfFilePath);
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        Paragraph title = new Paragraph("Данные класса " + titleText + ":");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addClassFieldsToPdf(PdfWriter writer,
                                     Object classObject) throws IllegalAccessException, DocumentException {
        ColumnText columnTextFields = new ColumnText(writer.getDirectContent());
        columnTextFields.setSimpleColumn(36, 36, 299, 700);

        ColumnText columnTextValues = new ColumnText(writer.getDirectContent());
        columnTextValues.setSimpleColumn(305, 36, 559, 700);

        Field[] fields = classObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(classObject);

            columnTextFields.addElement(new Paragraph(field.getName()));
            columnTextValues.addElement(new Paragraph(value != null ? value.toString() : "null"));
        }

        columnTextFields.go();
        columnTextValues.go();
    }

    private String createPdfFilePath(String simpleName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String fileName = simpleName + "_" + formattedDateTime + ".pdf";
        String directoryPath = "pdf";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directoryPath + File.separator + fileName;
    }
}

