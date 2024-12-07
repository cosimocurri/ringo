package com.example.ringo_star.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.ringo_star.R;
import com.example.ringo_star.data.entity.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;

public class PDFUtils {
    public static void createPdf(Context context, User user, String llmResponse) {
        File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File pdfFile = new File(publicDir, "report.pdf");

        try {
            PdfWriter writer = new PdfWriter(Files.newOutputStream(pdfFile.toPath()));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_pdf);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] logoBytes = stream.toByteArray();

            ImageData imageData = ImageDataFactory.create(logoBytes);
            Image logo = new Image(imageData).setWidth(235).setHeight(80);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String birthday = user.getBirthday().format(formatter);

            String height = user.getHeight() + " cm";
            String weight = user.getWeight() + " kg";
            String smoke = user.isSmoke() ? "Smoke" : "Doesn't smoke";

            String bloodGroup = user.getBloodGroup().toString()
                    .replace("zero", "0")
                    .replace("_positive", "+")
                    .replace("_negative", "-");

            Paragraph paragraphUserInformation = new Paragraph("User information").setFontSize(16).setBold().setMargins(20,  0, 0, 0);
            Paragraph paragraphName = new Paragraph(user.getFirstname() + " " + user.getLastname()).setMargins(0, 0, 0 , 20);
            Paragraph paragraphGender = new Paragraph(capitalizeFirstLetter(user.getGender().toString())).setMargins(0, 0, 0 , 20);
            Paragraph paragraphBirthday = new Paragraph(birthday).setMargins(0, 0, 0 , 20);
            Paragraph paragraphHeight = new Paragraph(height).setMargins(0, 0, 0 , 20);
            Paragraph paragraphWeight = new Paragraph(weight).setMargins(0, 0, 0 , 20);
            Paragraph paragraphBloodGroup = new Paragraph(bloodGroup).setMargins(0, 0, 0 , 20);
            Paragraph paragraphSmoke = new Paragraph(smoke).setMargins(0, 0, 0 , 20);

            document.add(logo);

            document.add(paragraphUserInformation);
            document.add(paragraphName);
            document.add(paragraphGender);
            document.add(paragraphBirthday);
            document.add(paragraphHeight);
            document.add(paragraphWeight);
            document.add(paragraphBloodGroup);
            document.add(paragraphSmoke);

            Paragraph paragraphResponseTitle = new Paragraph("LLM response").setFontSize(16).setBold().setMargins(20,  0, 0, 0);
            Paragraph paragraphResponse = new Paragraph(llmResponse).setMargins(0, 0, 0 , 20);

            document.add(paragraphResponseTitle);
            document.add(paragraphResponse);

            Paragraph paragraphGraphInformation = new Paragraph("Graph information (about user, questionnaires and variations of weight, smoke, etc.)").setFontSize(16).setBold().setMargins(20,  0, 0, 0);
            document.add(paragraphGraphInformation);

            Model model = RingoStarRDF4J.loadModelFromFile(context, "kg.ttl");

            int index = 1;

            for(Statement statement : model) {
                String subject = statement.getSubject().stringValue();
                String predicate = statement.getPredicate().stringValue();
                String object = statement.getObject().stringValue();

                Paragraph paragraph = new Paragraph(index + ") " + subject + " " + predicate + " " + object).setMargins(0, 0, 0 , 20);
                document.add(paragraph);

                index++;
            }

            document.close();
        } catch(Exception ignored) {}
    }

    public static String capitalizeFirstLetter(String input) {
        if(input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}