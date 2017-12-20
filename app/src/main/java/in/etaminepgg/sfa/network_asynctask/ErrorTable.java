package in.etaminepgg.sfa.network_asynctask;


public class ErrorTable {

/*

    public boolean isError(final Context context, String response) {

        NetworkChecker networkChecker = new NetworkChecker();
        try {
            Alert alertUI = new Alert();
            if (!networkChecker.haveNetworkConnection() && response.startsWith("Failed")) {
                if (ActiveActivitiesTracker.showNetworkErrorMessage()) {
                    Toast.makeText(context, context.getResources().getString(R.string.messageIfNoInternetConnection), Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (networkChecker.haveNetworkConnection() && response.startsWith("Failed")) {
                return true;
            }


            JSONObject mErrorObject = new JSONObject(response);
            String errorCode = mErrorObject.getString("errorcode");

            switch (errorCode) {
                case "10001":
                    alertUI.showAlert(context.getResources().getString(R.string.its404), context.getResources().getString(R.string.oopsSomethingWentWrong), context, "10001");
                    return true;

                case "10002":
                    alertUI.showAlert(context.getResources().getString(R.string.message), context.getResources().getString(R.string.checkYourInputs), context, "10002");
                    return true;

                case "10003":
                    alertUI.showAlert(context.getResources().getString(R.string.alert), context.getResources().getString(R.string.invalidRequest), context, "10003");
                    return true;
                //TODO: this should be info
                case "10004":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.information), context.getResources().getString(R.string.messageMobileNotRegistered), context, "10004");
                    return true;

                case "10005":
                    alertUI.showAlert(context.getResources().getString(R.string.oops), context.getResources().getString(R.string.messageCheckCoverage), context, "10005");
                    return true;

                case "10006":
                    alertUI.showAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.messageIfSessionExpired), context, "10006");
                    return true;

                case "10007":
                    alertUI.showAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.messageIfSessionExpiredAgain), context, "10007");

                case "10008":
                    alertUI.showAlert(context.getResources().getString(R.string.message), context.getResources().getString(R.string.messageIfInvalidOTP), context, "10008");
                    return true;

                case "10009":
                    alertUI.showAlert(context.getResources().getString(R.string.message), context.getResources().getString(R.string.messageIfMobileNumberDeactivatedBySchool), context, "10009");
                    return true;

                case "10010":
                    alertUI.showAlert(context.getResources().getString(R.string.message), context.getResources().getString(R.string.messageIfServiceSuspendedBySchool), context, "10010");
                    return true;

                case "10011":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.information), context.getResources().getString(R.string.messageMobileNotRegistered), context, "10011");
                    return true;

                case "10015":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.uploadingMediaFailed), context, "10015");
                    return true;

                case "10016":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.messageIfCurrentAcademicSetIsNotInSchool), context, "10016");
                    return true;

                case "10017":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.createdStoryFailed), context, "10017");
                    return true;

                case "10018":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.wrongInputs), context.getResources().getString(R.string.failedToCreatePrivateStory), context, "10018");
                    return true;

                case "10012":
                    alertUI.showWarningAlert(context.getResources().getString(R.string.sorry), context.getResources().getString(R.string.notAuthorized), context, "10012");
                    return true;
                default:
                    return false;
            }

        } catch (Exception e) {
              return response == null || response.equals("");
        }

    }
*/

}
