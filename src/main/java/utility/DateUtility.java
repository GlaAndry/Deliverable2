package utility;

import java.util.logging.Logger;

public class DateUtility {

    private static final Logger LOGGER = Logger.getLogger(DateUtility.class.getName());


    public String determineMonth(String str){

        String ret;
        ret = determineMonth1(str);
        if(ret.equals("")){
            ret = determineMonth2(str);
            if(ret.equals("")){
                ret = determineMonth3(str);
            }
        }

        return ret;

    }


    private String determineMonth1(String str){

        String ret = "";
        switch (str) {
            case "Jan":
                ret = "01-";
                break;
            case "Feb":
                ret = "02-";
                break;
            case "Mar":
                ret = "03-";
                break;
            case "Apr":
                ret = "04-";
                break;

            default:
                LOGGER.info("Non trovato");
        }

        return ret;
    }

    private String determineMonth2(String string){

        String ret1 = "";
        switch (string) {
            case "May":
                ret1 = "05-";
                break;
            case "Jun":
                ret1 = "06-";
                break;
            case "Jul":
                ret1 = "07-";
                break;
            case "Aug":
                ret1 = "08-";
                break;
            default:
                LOGGER.info("Non trovato");
        }

        return ret1;

    }

    private String determineMonth3(String string2){

        String ret2 = "";
        switch (string2) {
            case "Sep":
                ret2 = "09-";
                break;
            case "Oct":
                ret2 = "10-";
                break;
            case "Nov":
                ret2 = "11-";
                break;
            case "Dec":
                ret2 = "12-";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + string2);

        }
        return ret2;

    }
}
