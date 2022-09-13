package com.powerpuff.registrationservice;


import com.powerpuff.registrationservice.token.ConfirmationToken;
import com.powerpuff.registrationservice.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final ConfirmationTokenService confirmationTokenService;
    private EmailValidator emailValidator;
//    private final EmailSender emailSender;
    private RestTemplate restTemplate;

    public String register(RegistrationRequest request) {
        boolean isValid = emailValidator.test(request.getEmail());

        if (!isValid) {
            throw new IllegalStateException("email not valid");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", request.getFirstName());
        jsonObject.put("lastName", request.getLastName());
        jsonObject.put("email", request.getEmail());
        jsonObject.put("password", request.getPassword());

        HttpEntity<String> requestBody = new HttpEntity<>(jsonObject.toString(), headers);

        String message =restTemplate.postForObject("http://localhost:8081/user/save", requestBody, String.class);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                request.getEmail()
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
//        String link = "http://localhost:8080/sign-up/confirm?token=" + token;
//        emailSender.send(request.getEmail(), buildEmail(request.getFirstName(), link));
        return message + ", token is: " + token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        String message = restTemplate.getForObject("http://localhost:8081/user/enable/" + confirmationToken.getUserEmail()
        , String.class);
        return message;
    }

//    private String buildEmail(String name, String link) {
//        return "<!DOCTYPE html><html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\"><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]--><style>\n" +
//                "*{box-sizing:border-box}body{margin:0;padding:0}a[x-apple-data-detectors]{color:inherit!important;text-decoration:inherit!important}#MessageViewBody a{color:inherit;text-decoration:none}p{line-height:inherit}.desktop_hide,.desktop_hide table{mso-hide:all;display:none;max-height:0;overflow:hidden}@media (max-width:520px){.desktop_hide table.icons-inner{display:inline-block!important}.icons-inner{text-align:center}.icons-inner td{margin:0 auto}.row-content{width:100%!important}.mobile_hide{display:none}.stack .column{width:100%;display:block}.mobile_hide{min-height:0;max-height:0;max-width:0;overflow:hidden;font-size:0}.desktop_hide,.desktop_hide table{display:table!important;max-height:none!important}}\n" +
//                "</style></head><body style=\"background-color:#fff;margin:0;padding:0;-webkit-text-size-adjust:none;text-size-adjust:none\"><table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0;background-color:#fff\"><tbody><tr><td><table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tbody><tr><td><table \n" +
//                "class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0;color:#000;width:500px\" width=\"500\"><tbody><tr><td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace:0;mso-table-rspace:0;font-weight:400;text-align:left;vertical-align:top;padding-top:5px;padding-bottom:5px;border-top:0;border-right:0;border-bottom:0;border-left:0\"><table class=\"heading_block block-1\" width=\"100%\" border=\"0\" \n" +
//                "cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tr><td class=\"pad\" style=\"width:100%;text-align:center\"><h1 style=\"margin:0;color:#555;font-size:23px;font-family:Arial,Helvetica Neue,Helvetica,sans-serif;line-height:120%;text-align:center;direction:ltr;font-weight:700;letter-spacing:normal;margin-top:0;margin-bottom:0\"><span class=\"tinyMce-placeholder\">Hello, " + name + ", click here to confirm your email!</span></h1></td></tr></table><table \n" +
//                "class=\"button_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tr><td class=\"pad\"><div class=\"alignment\" align=\"center\">\n" +
//                "<!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"" + link + "\" style=\"height:38px;width:80px;v-text-anchor:middle;\" arcsize=\"11%\" stroke=\"false\" fillcolor=\"#3AAEE0\"><w:anchorlock/><v:textbox inset=\"0px,0px,0px,0px\"><center style=\"color:#ffffff; font-family:Arial, sans-serif; font-size:14px\"><![endif]-->\n" +
//                "<a href=\"" + link + "\" target=\"_blank\" style=\"text-decoration:none;display:inline-block;color:#ffffff;background-color:#3AAEE0;border-radius:4px;width:auto;border-top:1px solid #3AAEE0;font-weight:400;border-right:1px solid #3AAEE0;border-bottom:1px solid #3AAEE0;border-left:1px solid #3AAEE0;padding-top:5px;padding-bottom:5px;font-family:Arial, Helvetica Neue, Helvetica, sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all;\"><span style=\"padding-left:20px;padding-right:20px;font-size:14px;display:inline-block;letter-spacing:normal;\"><span style=\"line-height: 28px;\">Confirm</span></span></a>\n" +
//                "<!--[if mso]></center></v:textbox></v:roundrect><![endif]--></div></td></tr></table></td></tr></tbody></table></td></tr></tbody></table><table class=\"row row-2\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tbody><tr><td><table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0;color:#000;width:500px\" \n" +
//                "width=\"500\"><tbody><tr><td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace:0;mso-table-rspace:0;font-weight:400;text-align:left;vertical-align:top;padding-top:5px;padding-bottom:5px;border-top:0;border-right:0;border-bottom:0;border-left:0\"><table class=\"icons_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tr><td class=\"pad\" \n" +
//                "style=\"vertical-align:middle;color:#9d9d9d;font-family:inherit;font-size:15px;padding-bottom:5px;padding-top:5px;text-align:center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0;mso-table-rspace:0\"><tr><td class=\"alignment\" style=\"vertical-align:middle;text-align:center\">\n" +
//                "<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]--><!--[if !vml]><!--><table class=\"icons-inner\" style=\"mso-table-lspace:0;mso-table-rspace:0;display:inline-block;margin-right:-4px;padding-left:0;padding-right:0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"><!--<![endif]--><tr><td \n" +
//                "style=\"vertical-align:middle;text-align:center;padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:6px\"><a href=\"https://www.designedwithbee.com/\" target=\"_blank\" style=\"text-decoration: none;\"><img class=\"icon\" alt=\"Designed with BEE\" src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/53601_510656/Signature/bee.png\" height=\"32\" width=\"34\" align=\"center\" style=\"display:block;height:auto;margin:0 auto;border:0\"></a></td><td \n" +
//                "style=\"font-family:Arial,Helvetica Neue,Helvetica,sans-serif;font-size:15px;color:#9d9d9d;vertical-align:middle;letter-spacing:undefined;text-align:center\"><a href=\"https://www.designedwithbee.com/\" target=\"_blank\" style=\"color: #9d9d9d; text-decoration: none;\">Designed with BEE</a></td></tr></table></td></tr></table></td></tr></table></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table><!-- End --></body></html>";
//    }

}
