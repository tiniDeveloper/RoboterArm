package de.developup.roboterarm.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ARPScanner {

    public static ArrayList<ArrayList<String>> showDevices(){

        ArrayList<ArrayList<String>> devices = new ArrayList<ArrayList<String>>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("arp", "-a");
            Process process = processBuilder.start();
            // Lesen die Ausgabe des ARP-Befehls
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            String strPattern[] = {
                    "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"
            };

            while ((line = reader.readLine()) != null) {
                // Analysieren und verarbeiten Sie die Ausgabezeilen
                System.out.println(line);
                if (line.contains("dynamisch")||line.contains("dynamic")) {
                    for (String s : strPattern) {
                        Pattern pattern = Pattern.compile(s);
                        // Extracting all Patterns from the string
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            String hostname = "";
                            try {
                                InetAddress inetAddress = InetAddress.getByName(matcher.group());
                                hostname = inetAddress.getHostName();
                            } catch (UnknownHostException e) {
                                System.err.println("Host für IP-Adresse " + matcher.group() + " nicht gefunden: " + e.getMessage());
                            }
                            ArrayList<String> hm = new ArrayList<String>();
                            hm.add(hostname);
                            hm.add(matcher.group());
                            devices.add(hm);
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }

}
