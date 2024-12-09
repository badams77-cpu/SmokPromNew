$(document).ready(function() {

    //
    // New password key press.
    //

    $("#neww").keydown(function() {
        passwordFieldChange($(this).val());
    });

    //
    // Form submission.
    //

    $("#btn-change-password").click(function(e) {
        e.preventDefault();
        $("#user-change-password-form").submit();
    });

    //
    // Password Strength.
    //

    function passwordFieldChange(value) {
        if (value.length == 0) {
            displayPasswordStrength(0);
            $("#password-warning").hide();
            $("#password-suggestions").hide();
        } else {
            doPasswordStrength(value);
        }
    }

    function doPasswordStrength(password) {
        var zxcvbnObj = zxcvbn(password);
        displayPasswordStrength(zxcvbnObj.score);
        displayPasswordWarning(zxcvbnObj.feedback.warning);
        displayPasswordFeedback(zxcvbnObj.feedback.suggestions);
    }

    function displayPasswordStrength(score) {

        var greenClass = "ps-green";
        var whiteClass = "ps-white";

        $("span#ps-1").removeClass(greenClass);
        $("span#ps-2").removeClass(greenClass);
        $("span#ps-3").removeClass(greenClass);
        $("span#ps-4").removeClass(greenClass);

        if (score > 0) {
            $("span#ps-1").addClass(greenClass);
        }

        if (score > 1) {
            $("span#ps-2").addClass(greenClass);
        }

        if (score > 2) {
            $("span#ps-3").addClass(greenClass);
        }

        if (score > 3) {
            $("span#ps-4").addClass(greenClass);
        }

    }

    function displayPasswordWarning(warning) {
        if (warning.length > 0) {
            $("#password-warning span").html(warning);
            $("#password-warning").show();
        } else {
            $("#password-warning").hide();
        }
    }

    function displayPasswordFeedback(suggestions) {
        if (suggestions.length > 0) {
            $("#password-suggestions span").html(suggestions.join("<br/>"));
            $("#password-suggestions").show();
        } else {
            $("#password-suggestions").hide();
        }
    }
});
