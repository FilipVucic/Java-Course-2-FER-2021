package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoSmartScriptEngine {

    public static void main(String[] args) {
        demo5();
    }

    private static void demo1() {
        String documentBody = null;
        try {
            documentBody = Files.readString(Path.of("webroot/scripts/osnovni.smscr"));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    private static void demo2() {
        String documentBody = null;
        try {
            documentBody = Files.readString(Path.of("webroot/scripts/zbrajanje.smscr"));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        parameters.put("a", "4");
        parameters.put("b", "2");
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    private static void demo3() {
        String documentBody = null;
        try {
            documentBody = Files.readString(Path.of("webroot/scripts/brojPoziva.smscr"));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        persistentParameters.put("brojPoziva", "3");
        RequestContext rc = new RequestContext(System.out, parameters, persistentParameters,
                cookies);
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(), rc
        ).execute();
        System.out.println("Vrijednost u mapi: " + rc.getPersistentParameter("brojPoziva"));
    }

    private static void demo4() {
        String documentBody = null;
        try {
            documentBody = Files.readString(Path.of("webroot/scripts/fibonacci.smscr"));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    private static void demo5() {
        String documentBody = null;
        try {
            documentBody = Files.readString(Path.of("webroot/scripts/fibonaccih.smscr"));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }
}
