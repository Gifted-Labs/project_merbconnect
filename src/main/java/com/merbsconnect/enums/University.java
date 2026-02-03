package com.merbsconnect.enums;

import lombok.Getter;

/**
 * Enum representing universities in Ghana.
 */
@Getter
public enum University {
    UG("University of Ghana"),
    KNUST("Kwame Nkrumah University of Science and Technology"),
    UCC("University of Cape Coast"),
    UEW("University of Education, Winneba"),
    UDS("University for Development Studies"),
    UPSA("University of Professional Studies, Accra"),
    UMAT("University of Mines and Technology"),
    UHAS("University of Health and Allied Sciences"),
    UENR("University of Energy and Natural Resources"),
    GIMPA("Ghana Institute of Management and Public Administration"),
    GCTU("Ghana Communication Technology University"),
    AAMUSTED("Akenten Appiah-Menka University of Skills Training and Entrepreneurial Development"),
    CKT_UTAS("C. K. Tedam University of Technology and Applied Sciences"),
    SDD_UBIDS("Simon Diedong Dombo University of Business and Integrated Development Studies"),
    ASHESI("Ashesi University"),
    CENTRAL("Central University"),
    VALLEY_VIEW("Valley View University"),
    PENTECOST("Pentecost University"),
    WISCONSIN("Wisconsin International University College"),
    METHODIST("Methodist University Ghana"),
    PRESBYTERIAN("Presbyterian University, Ghana"),
    ACADEMIC_CITY("Academic City University College"),
    LANCASTER("Lancaster University Ghana"),
    WEBSTER("Webster University Ghana"),
    AUCC("African University College of Communications"),
    STU("Sunyani Technical University"),
    TTU("Takoradi Technical University"),
    HTU("Ho Technical University"),
    KTU("Koforidua Technical University"),
    KsTU("Kumasi Technical University"),
    ATU("Accra Technical University"),
    OTHER("Other");

    private final String displayName;

    University(String displayName) {
        this.displayName = displayName;
    }
}
