package myMain;


import myClasses.RizpaStockExchangeDescriptor;
import myClasses.RseOffer;
import myClasses.RseStock;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/* testers
C:\Users\max95\Downloads\ex1-small.xml
C:\Users\max95\Downloads\ex1-error-3.3.xml
C:\Users\max95\Downloads\ex1-error-3.2.xml
*/
public class Main {

    private static RizpaStockExchangeDescriptor myRSED=null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int in = 1;
        System.out.println("welcome!!\nPlease select number of desired action");

        while (in != 6) {
            System.out.println("1. Load XML File");
            System.out.println("2. Print all Stocks");
            System.out.println("3. Print specific Stock");
            System.out.println("4. Trade Stocks");
            System.out.println("5. print all Trades and offers");
            System.out.println("6. Exit program");
            System.out.println("--------");
            try {
                in = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
                in = 0;
            }
            System.out.println("--------");
            if (myRSED == null && in > 1 && in !=6)
                in = 7;
            switch (in) {
                case 1:
                    loadXMLFile();
                    break;
                case 2:
                    printAllStocks();
                    break;
                case 3:
                    printSingleStock();
                    break;
                case 4:
                    trading();
                    break;
                case 5:
                    printAllOffers();
                    break;
                case 6:
                    System.out.println("Program exited");
                    break;
                case 7:
                    System.out.println("No File Has been loaded");
                    break;
                default:
                    System.out.println("Action doesn't exist");
                    break;
            }
            System.out.println("--------");
        }
    }

    //Gets full file path and loads XML file, if meets criteria, to engine
    public static void loadXMLFile() {
        System.out.println("Please enter full file path");

        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        if (!str.toLowerCase().endsWith(".xml")) {
            System.out.println("File path doesn't end with .xml");
            return;
        }
        try (InputStream inputStream = new FileInputStream(new File(str))) {
            RizpaStockExchangeDescriptor rSED = new RizpaStockExchangeDescriptor(inputStream);
            str = rSED.checkRSED();
            if (str == null) {
                myRSED = rSED;
                System.out.println("Load successful");
            } else
                System.out.println(str);

        } catch (JAXBException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println("File doesn't exist or cannot be found");
            //System.out.println(e.toString());
        }
    }

    //Prints all stocks if file exists
    //else prints an error message
    public static void printAllStocks() {
        for (RseStock r : myRSED.getRseStocks().getRseStock()) {
            System.out.println(r.toString() + '\n');
        }
    }

    //Prints stocks if file exists & symbol exists
    //else prints an error message
    public static void printSingleStock() {
        System.out.println("Please enter stock symbol");
        Scanner scanner = new Scanner(System.in);
        String in = scanner.nextLine();
        RseStock r = myRSED.findStock(in);
        if (r != null) {
            System.out.println(r.toString());
            List<RseOffer> lst = r.getDealsMade().getRseOffer();
            if (lst.size() == 0)
                System.out.println("No deals made");
            else
                System.out.println("Deals made:");
            printRseOfferList(lst);
        } else
            System.out.println("No Stock has such symbol");
    }

    //gets all information from user to make trade and passes along to proper function
    //if there is an error on input user is notified and exited to main menu
    public static void trading() {
        Scanner scanner = new Scanner(System.in);
        int action, buySell, amountTrading;
        RseStock stock;

        //get stock
        System.out.println("Enter Symbol of stocks to Trade");
        String   symbol = scanner.nextLine();
        stock = myRSED.findStock(symbol);
        if (stock == null) {
            System.out.println("No Stock has such symbol");
            return;
        }

        //get action
        System.out.println("Select action by number:");
        System.out.println("1. LMT");
        System.out.println("2. MKT");
        try {
            action = scanner.nextInt();
            if (action < 0 || action > 2) {
                System.out.println("Action doesn't exist");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Action doesn't exist");
            return;
        }

        //get if buy or sell
        System.out.println("Select action by number:");
        System.out.println("1. Buy stocks");
        System.out.println("2. Sell Stocks");
        try {
            buySell = scanner.nextInt();
            if (buySell < 0 || buySell > 2) {
                System.out.println("Action doesn't exist");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Action doesn't exist");
            return;
        }

        //get amount
        System.out.println("Enter amount of stocks to Trade (Enter Integer)");
        try {
            amountTrading = scanner.nextInt();
            if (amountTrading < 1) {
                System.out.println("Amount must be an Integer larger than 0");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Amount must be an Integer larger than 0");
            return;
        }

        //send to relevant action
        if (action == 1)
            limit(buySell, amountTrading, stock);
        else
            market(buySell, amountTrading, stock);
    }

    //buy/sell at fixed price
    public static void limit(int action, int amountTrading, RseStock stock) {
        Scanner scanner = new Scanner(System.in);
        int price;

        //get price
        System.out.println("Enter Price per stock to Trade (Enter Integer)");
        try {
            price = scanner.nextInt();
            if (price < 1) {
                System.out.println("Price must be an Integer larger than 0");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Price must be an Integer larger than 0");
            return;
        }

        //add offer to stock
        List<String> res = stock.addOffer(action, new RseOffer(amountTrading, price));
        //print results
        for (String str : res) {
            System.out.println(str);
        }
    }

    //buy/sell according to market prices
    public static void market(int action, int amountTrading, RseStock stock) {
        //add offer to stock
        List<String> res = stock.addOffer(action, new RseOffer(amountTrading, RseOffer.MKT_CODE));
        //print results
        for (String str : res) {
            System.out.println(str);
        }
    }

    //prints all offers and deals made for all stocks
    public static void printAllOffers() {
        long total;
        for (RseStock r : myRSED.getRseStocks().getRseStock()) {
            System.out.println("For " + r.getRseSymbol().toUpperCase() + ":");

            //print Buy list for stock
            if (r.getBuyOffers().getRseOffer().size() > 0) {
                System.out.println("Awaiting Buy:");
                total = printRseOfferList(r.getBuyOffers().getRseOffer());
                System.out.println("Sum of trade deals to be made (Buying): " + total + "\n");
            } else
                System.out.println("Buy list is empty");

            System.out.println();

            //print Sell list for stock
            if (r.getSellOffers().getRseOffer().size() > 0) {
                System.out.println("Awaiting Sell:");
                total = printRseOfferList(r.getSellOffers().getRseOffer());
                System.out.println("Sum of trade deals to be made (Selling): " + total + "\n");
            } else
                System.out.println("Sell list is empty");

            System.out.println();

            //print DealsMade list for stock
            if (r.getDealsMade().getRseOffer().size() > 0) {
                System.out.println("Trades made:");
                total = printRseOfferList(r.getDealsMade().getRseOffer());
                System.out.println("Sum of trade deals made: " + total + "\n");
            } else
                System.out.println("Done deals list is empty");
            System.out.println();
        }
    }

    //prints an offer list and returns total of amount * price
    public static long printRseOfferList(List<RseOffer> lst){
        long total = 0;
        for (RseOffer o : lst) {
            System.out.println(o.toString());
            total += (long) o.getAmount() * o.getPrice();
        }
        return total;
    }

}