/**
* This class provides full functionality of Bittrex 3.1 API (BETA)
*
* @author  Bartłomiej Woźniak
* @version 0.9
* @since   2019-05-06
*/

import main.java.ReconnectionAttemptsExceededException;
import main.java.SHA512;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Bittrexv3 {
    public static final int DEFAULT_RETRY_ATTEMPTS = 1;
    public static final int DEFAULT_RETRY_DELAY = 15;
    private final String API_VERSION = "3", INITIAL_URL = "https://api.bittrex.com/";
    private final String encryptionAlgorithm = "HmacSHA512";
    private String apikey;
    private String secret;
    private final int retryAttempts;
    private int retryAttemptsLeft;
    private final int retryDelaySeconds;



    public Bittrexv3(int retryAttempts, int retryDelaySeconds) {
        this.apikey = "";
        this.secret = "";
        this.retryAttempts = retryAttempts;
        this.retryDelaySeconds = retryDelaySeconds;
        retryAttemptsLeft = retryAttempts;
    }

    public Bittrexv3() {

        this(DEFAULT_RETRY_ATTEMPTS, DEFAULT_RETRY_DELAY);
    }
    private String generateUrl(String action) {
        String url = INITIAL_URL;
        url += "v" + API_VERSION +"/";
        url += action;
        return url;
    }
    private String generateUrl(String action, String parameter) {
        String url = INITIAL_URL;
        url += "v" + API_VERSION +"/";
        url += action + "/";
        url += parameter;
        return url;
    }
    private String generateUrl(String action, String parameter, String parameter2) {
        String url = INITIAL_URL;
        url += "v" + API_VERSION +"/";
        url += action + "/";
        url += parameter + "/";
        url += parameter2;
        return url;
    }
    private String getResponseBody(final String url, String method) throws NoSuchAlgorithmException {
        long timestamp = new Date().getTime();
        String content="";
        String signature="";
        String contentHash = SHA512.get_SHA_512_SecurePassword(content);
        String preSign = String.join("", Long.toString(timestamp), url, method, contentHash);
        try {
            Mac sha_HMAC = Mac.getInstance(encryptionAlgorithm);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), encryptionAlgorithm);
            sha_HMAC.init(secret_key);
            signature = DatatypeConverter.printHexBinary(sha_HMAC.doFinal(preSign.getBytes())).toLowerCase();
        }
        catch (Exception e){
            System.out.println("Error");
        }
        String result = null;
        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("Api-Key", apikey);
            request.addHeader("Api-Timestamp",Long.toString(timestamp));
            request.addHeader("Api-Signature", signature);
            request.addHeader("Api-Content-Hash", contentHash);
            HttpResponse httpResponse = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer resultBuffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null)
                resultBuffer.append(line);
            result = resultBuffer.toString();
        } catch (UnknownHostException e) {
            if(retryAttemptsLeft-- > 0) {
                System.err.printf("Could not connect to host - retrying in %d seconds... [%d/%d]%n", retryDelaySeconds, retryAttempts - retryAttemptsLeft, retryAttempts);
                try {
                    Thread.sleep(retryDelaySeconds * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                result = getResponseBody(url, method);
            } else {
                throw new ReconnectionAttemptsExceededException("Maximum amount of attempts to connect to host exceeded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            retryAttemptsLeft = retryAttempts;
        }
        return result;
    }
    public String getAccount() {
        String result ="false";
        String request_url = generateUrl("account");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getAdresses() {
        String result ="false";
        String request_url = generateUrl("addresses");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getAdresses(String currency) {
        String result ="false";
        String request_url = generateUrl("addresses", currency);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getBalances() {
        String result ="false";
        String request_url = generateUrl("balances");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getBalances(String currency) {
        String result ="false";
        String request_url = generateUrl("balances", currency);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getCurrencies() {
        String result ="false";
        String request_url = generateUrl("currencies");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getCurrencies(String currency) {
        String result ="false";
        String request_url = generateUrl("currencies", currency);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getDepositsOpen() {
        String result ="false";
        String request_url = generateUrl("deposits", "open");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getDepositsClosed() {
        String result ="false";
        String request_url = generateUrl("deposits", "closed");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getDepositsByTxId(String TxId) {
        String result ="false";
        String request_url = generateUrl("deposits", "ByTxId", TxId);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getDepositsByID(String depositId) {
        String result ="false";
        String request_url = generateUrl("deposits", depositId);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getMarkets() {
        String result ="false";
        String request_url = generateUrl("markets");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getMarketsSummaries() {
        String result ="false";
        String request_url = generateUrl("markets", "summaries");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getMarketsMarketSymbol(String MarketSymbol) {
        String result ="false";
        String request_url = generateUrl("markets", MarketSymbol);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getMarketsMarketSymbol(String MarketSymbol, String parameter) {
        String result ="false";
        String request_url = generateUrl("markets", MarketSymbol, parameter);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getOrdersClosed() {
        String result ="false";
        String request_url = generateUrl("orders","closed");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getOrdersOpen() {
        String result ="false";
        String request_url = generateUrl("orders","open");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getOrders(String orderid) {
        String result ="false";
        String request_url = generateUrl("orders",orderid);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String Ping() {
        String result ="false";
        String request_url = generateUrl("ping");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getSubaccounts() {
        String result ="false";
        String request_url = generateUrl("subaccounts");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getSubaccounts(String subaccountId) {
        String result ="false";
        String request_url = generateUrl("subaccounts", subaccountId);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getWithdrawalsOpen() {
        String result ="false";
        String request_url = generateUrl("withdrawals", "open");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getWithdrawalsClosed() {
        String result ="false";
        String request_url = generateUrl("withdrawals", "closed");
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getWithdrawalsByTxId(String TxId) {
        String result ="false";
        String request_url = generateUrl("withdrawals", "ByTxId",TxId);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String getWithdrawalsByID(String withdrawalId) {
        String result ="false";
        String request_url = generateUrl("withdrawals", withdrawalId);
        try {
            result = getResponseBody(request_url,"GET") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
    public String postAdresses(String currencySymbol){
        String result ="false";
        String request_url = generateUrl("addresses", currencySymbol);
        try {
            result = getResponseBody(request_url,"POST") ;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
}
