//package model;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SessionImplementor;
//import org.hibernate.id.IdentifierGenerator;
//
//import javax.net.ssl.HttpsURLConnection;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Serializable;
//import java.net.*;
//import java.sql.Timestamp;
//
//import java.util.Date;
//import java.util.Random;
//
///**
// * Created by HUBKB.S on 11/18/2017.
// */
//public class Student_Id_Generator {
//    @Override
//    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
//
//        String  postfix = "17" ;
//
//
//        //generate a random number
//        Random random= new Random();
//        int prefix = random.nextInt(1+100000);
//       // getDate();
//        getDate_https();
//        return prefix +postfix;
//    }
//
//    //default id when the table is empty
//    public String getDefault_id(){
//        return "11001";
//    }
//
//    /**
//     *  get the current date from google api
//     * @return
//     */
//    String getDate_Http(){
//        String get_url ="https://www.google.com.gh";
//        String USER_AGENT="chrome";
//        try {
//            URL url = new URL(get_url);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("User-Agent",USER_AGENT);
//            int responseCode = connection.getResponseCode();
//            System.out.print("this is the response code"+" "+responseCode);
//
//            //check if it was successful
//            if(responseCode==HttpURLConnection.HTTP_OK){
//                //success
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            }else {
//                System.out.print("HTTP error");
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    String getDate_https() {
//        String https_url="https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200&key=AIzaSyAML5qBAQ23iObM7JFvOM_TsSCin9-Cl8o\n";
//
//        final String proxy_name="";
//        final int port=3030;
//        int status=0;
//        URL url;
//        HttpsURLConnection connection=null;
//        try {
//            url = new URL(https_url);
//            if( proxy_name.isEmpty()){
//                connection = (HttpsURLConnection)url.openConnection();
//                status=connection.getResponseCode();
//            }
//            // <-- use this in case there is a proxy and a port -->
////            else{
////                Proxy proxy =new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_name,port));
////                connection = (HttpsURLConnection) url.openConnection(proxy);
////                Authenticator authenticator=new Authenticator() {
////                    @Override
////                    protected PasswordAuthentication getPasswordAuthentication() {
////                        return super.getPasswordAuthentication();
////                    }
////                };
////                Authenticator.setDefault(authenticator);
////                status=connection.getResponseCode();
////            }
//            if(status==HttpsURLConnection.HTTP_OK){
//                System.out.print("the connection was successful\n");
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String inputLine ;
//                StringBuffer buffer=new StringBuffer();
//                while ((inputLine=reader.readLine()) !=null){
//                    buffer.append(inputLine);
//                }
//                System.out.print(buffer);
//                JsonObject jsonObject = new JsonParser().parse(String.valueOf(buffer)).getAsJsonObject();
//                long timeStamp =1331161200;
//                int dstOffset=Integer.parseInt(String.valueOf(jsonObject.get("dstOffset").getAsString()));
//                int rawOffset=Integer.parseInt(String.valueOf(jsonObject.get("rawOffset").getAsString()));
//                reader.close();
//
//
//
//                long date_value =(timeStamp + (dstOffset+rawOffset));
//                //Calendar cal = Calendar.getInstance();
//                //cal.setTimeInMillis(date_value);
//
//              //  cal.add(Calendar.MILLISECOND,date_value);
//                //cal.setTimeInMillis(date_value);
//                Timestamp timestamp= new Timestamp(date_value);
//                Date date=timestamp;
//
//                System.out.print("\nThis is the dstoffset: "+" "+dstOffset+"\nThis is the raw offset: "+" "+rawOffset);
//
//                System.out.print("\nThis is the date:"+" "+date+"\n");
//            }else {
//                System.out.print("Sorry!... Connection could not be established.");
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
