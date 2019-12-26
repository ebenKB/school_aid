package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Receipt {
    public Receipt() {

        // create a receipt number for an instance of this class
        this.receiptNumber = this.generateReceipt();
    }
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "receipt_sequence", sequenceName = "receipt_sequence", allocationSize = 100)
    @GeneratedValue(generator = "receipt_sequence")
    private Long id;

    private String receiptNumber;

    @OneToOne(mappedBy = "receipt", cascade = CascadeType.ALL)
    TransactionLogger transaction;


    public String generateReceipt() {
        int counter = 0;
        LocalDate date = LocalDate.now();
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        String year [] = String.valueOf(date.getYear()).split("");
        int len = year.length;

        Random rand = new Random();
        int alpha = 0;
        do {
            alpha = rand.nextInt(90);
            counter++;
        }while (alpha < 65 || alpha == 79);

        int digits = rand.nextInt((999 - 1) + 1);
        String partB;

        // add extra zeros to form a string of three letters
        if(digits < 10) {
            partB = "00"+String.valueOf(digits);
        } else if(digits < 100) {
            partB = "00"+String.valueOf(digits);
        } else {
            partB = String.valueOf(digits);
        }
        return String.valueOf(day)+String.valueOf(month)+year[len-2]+year[len-1]+"-"+partB+((char)alpha);
    }
}
