import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PdfUtil {

    //TODO add file to list
    //checklist for file


    @Nonnull
    public static String getText(@Nonnull final File file) {
        final StringBuilder stringBuilder = new StringBuilder();

        try (final PdfReader pdfReader = new PdfReader(file.getAbsolutePath())) {
            final PdfTextExtractor pdfExtractor = new PdfTextExtractor(pdfReader);

            for (int page = 0; page <= pdfReader.getNumberOfPages(); page++) {
                stringBuilder.append(pdfExtractor.getTextFromPage(page));
                stringBuilder.append(System.lineSeparator());
            }

            return stringBuilder.toString();
        }catch (IOException e) {
            log.error("IOException on text reading");
        }
        return stringBuilder.toString();
    }

    @Nonnull
    public static String getMetaText(@Nonnull final File file) {
        try (final PdfReader pdfReader = new PdfReader(file.getAbsolutePath())) {
            return new String(pdfReader.getMetadata(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("IOException on meta text reading");
        }
        return "";
    }
}
