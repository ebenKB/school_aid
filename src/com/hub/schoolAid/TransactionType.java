package com.hub.schoolAid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public enum TransactionType {
    SCHOOL_FEES, FEEDING_FEE,OTHERS, SALES,
}
