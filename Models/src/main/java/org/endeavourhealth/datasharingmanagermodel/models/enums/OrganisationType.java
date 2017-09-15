package org.endeavourhealth.datasharingmanagermodel.models.enums;

public enum OrganisationType {
    GPPRACTICE((byte)0),
    NHSTRUST((byte)1),
    NHSTRUSTSITE((byte)2),
    PATHLAB((byte)3),
    BRANCH((byte)4),
    COMMISSIONINGREGION((byte)5),
    CARETRUST((byte)6),
    CARETRUSTSITE((byte)7),
    CCG((byte)8),
    CCGSITE((byte)9),
    CSU((byte)10),
    CSUSITE((byte)11),
    EDUCATION((byte)12),
    NHSHOSPICE((byte)13),
    NONNHSHOSPICE((byte)14),
    IOMGOVDIRECTORATE((byte)15),
    IOMGOVDEPT((byte)16),
    JUSTICEENTITY((byte)17),
    NONNHSORGANISATION((byte)18),
    NHSSUPPORTAGENGY((byte)19),
    OPTICALHQ((byte)20),
    OPTICALSITE((byte)21),
    OTHER((byte)22),
    PHARMACYHQ((byte)23),
    ISHP((byte)24),
    ISHPSITE((byte)25),
    PRISON((byte)26),
    SCHOOL((byte)27),
    SPECIALHEALTH((byte)28),
    LOCALAUTHORITY((byte)29),
    LOCALAUTHORITYSITE((byte)30),
    NIORG((byte)31),
    SCOTTISHGP((byte)32),
    SCOTTISHPROVIDER((byte)33),
    WALESHB((byte)34),
    WALESHBSITE((byte)35),
    DISPENSARY((byte)36),
    IOMGOVDIRECTORATESITE((byte)37);


    private byte organisationType;

    OrganisationType(byte organisationType) {
        this.organisationType = organisationType;
    }

    public byte getOrganisationType() {
        return organisationType;
    }
}
