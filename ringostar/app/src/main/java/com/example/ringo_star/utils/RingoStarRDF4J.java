package com.example.ringo_star.utils;

import android.content.Context;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.turtle.TurtleParserFactory;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.Literal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Optional;

public class RingoStarRDF4J {
    public static String ns = "http://example.org/";

    public static String nodeNS = ns + "node";
    public static String propertyNS = ns + "property";
    public static String relationNS = ns + "relation";

    public static IRI nodeUserIRI = Values.iri(nodeNS, "user");

    public static IRI propertyFirstnameIRI = Values.iri(propertyNS, "firstname");
    public static IRI propertyLastnameIRI = Values.iri(propertyNS, "lastname");
    public static IRI propertyGenderIRI = Values.iri(propertyNS, "gender");
    public static IRI propertyBirthdayIRI = Values.iri(propertyNS, "birthday");
    public static IRI propertyHeightIRI = Values.iri(propertyNS, "height");
    public static IRI propertyBloodGroupIRI = Values.iri(propertyNS, "bloodGroup");
    public static IRI propertyLastSmokeRecordIRI = Values.iri(propertyNS, "lastSmokeRecord");
    public static IRI propertyLastWeightRecordIRI = Values.iri(propertyNS, "lastWeightRecord");
    public static IRI propertyValue = Values.iri(propertyNS, "value");
    public static IRI propertyDate = Values.iri(propertyNS, "date");
    public static IRI propertyName = Values.iri(propertyNS, "name");
    public static IRI propertyText = Values.iri(propertyNS, "text");

    public static IRI relationCarry = Values.iri(relationNS, "carry");
    public static IRI relationCompile = Values.iri(relationNS, "compile");
    public static IRI relationHas = Values.iri(relationNS, "has");

    public static Optional<String> getLiteralValue(Model model, IRI node, IRI property) {
        return model.filter(node, property, null)
                .stream()
                .map(Statement::getObject)
                .filter(Value::isLiteral)
                .map(value -> ((Literal) value).stringValue())
                .findFirst();
    }

    public static void saveModelToFile(Model model, Context context, String filePath) {
        File file = new File(context.getFilesDir(), filePath);

        try(FileOutputStream out = new FileOutputStream(file)) {
            RDFParserRegistry.getInstance().add(new TurtleParserFactory());
            Rio.write(model, out, RDFFormat.TURTLE);
        } catch(Exception ignored) {}
    }

    public static boolean fileExists(Context context, String filePath) {
        File file = new File(context.getFilesDir(), filePath);
        return file.exists();
    }

    public static Model loadModelFromFile(Context context, String filePath) {
        File file = new File(context.getFilesDir(), filePath);

        if(!file.exists())
            return null;

        try(FileInputStream in = new FileInputStream(file)) {
            RDFParserRegistry.getInstance().add(new TurtleParserFactory());
            return Rio.parse(in, "", RDFFormat.TURTLE);
        } catch(Exception ignored) {
            return null;
        }
    }

    public static String stringifyModel(Model model) {
        StringWriter writer = new StringWriter();
        Rio.write(model, writer, RDFFormat.TURTLE);

        return writer.toString();
    }
}