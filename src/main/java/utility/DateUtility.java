package utility;

public class DateUtility {



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
        }

        return ret;
    }

    private String determineMonth2(String string){

        String ret = "";
        switch (string) {
            case "May":
                ret = "05-";
                break;
            case "Jun":
                ret = "06-";
                break;
            case "Jul":
                ret = "07-";
                break;
            case "Aug":
                ret = "08-";
                break;
        }

        return ret;

    }

    private String determineMonth3(String string){

        String ret = "";
        switch (string) {
            case "Sep":
                ret = "09-";
                break;
            case "Oct":
                ret = "10-";
                break;
            case "Nov":
                ret = "11-";
                break;
            case "Dec":
                ret = "12-";
                break;

        }
        return ret;

    }
}
