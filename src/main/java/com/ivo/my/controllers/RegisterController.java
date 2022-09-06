package com.ivo.my.controllers;

import com.ivo.my.models.entities.PasswordResetToken;
import com.ivo.my.models.entities.User;
import com.ivo.my.models.entities.VerificationToken;
import com.ivo.my.services.SecurityQuestionService;
import com.ivo.my.services.UserService;
import com.ivo.my.services.TokenService;
import com.ivo.my.utils.validations.errors.EmailExistsException;
import com.ivo.my.utils.validations.errors.LoginNameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    SecurityQuestionService securityQuestionService;

    @GetMapping(value = "signup")
    public ModelAndView getRegisterPage() {
        final Map<String, Object> model = new HashMap<>();
        model.put("user", new User());
        model.put("questions", securityQuestionService.getQuestionDefinitions());
        return new ModelAndView("registrationPage", model);
    }

    @RequestMapping("/user/register")
    public ModelAndView processRegisterPage(@Valid User user,
                                            BindingResult result,
                                            @RequestParam Long questionId,
                                            @RequestParam String answer,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return getRegistrationModelAndView(user, questionId, answer);
        }
        try {
            user = userService.registerNewUser(user, request, questionId, answer);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return getRegistrationModelAndView(user, questionId, answer);
        } catch (LoginNameExistsException e) {
            result.addError(new FieldError("user", "loginName", e.getMessage()));
            return getRegistrationModelAndView(user, questionId, answer);
        } catch (Exception err) {
            userService.deleteUser(user.getId());
            throw err;
        }
        redirectAttributes.addFlashAttribute("globalMessage", "Pls verify your account by email.");
        return new ModelAndView("redirect:/login");
    }

    private ModelAndView getRegistrationModelAndView(User user, Long questionId, String answer) {
        return new ModelAndView("registrationPage", "user", user)
                .addObject("questions", securityQuestionService.getQuestionDefinitions())
                .addObject("questionId", questionId)
                .addObject("answer", answer);
    }

    @RequestMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(@RequestParam("token") String token,
                                            RedirectAttributes redirectAttributes) {

        VerificationToken verificationToken = tokenService.findVerificationToken(token);
        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your doesn't have access token, pls register again.");
            return new ModelAndView("redirect:/login");
        }

        if (verificationToken.isExpired()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your token is expired, pls register again.");
            return new ModelAndView("redirect:/login");
        }

        userService.enableUser(verificationToken.getUser().getId());
        redirectAttributes.addFlashAttribute("globalMessage", "Your accaunt verified successfully.");
        // was for 2fa auth
//        return new ModelAndView("qrcode", "user", userService.findUserById(verificationToken.getUser().getId()));
        return new ModelAndView("redirect:/login");
    }

    @PostMapping(value = "/user/resetPassword")
    @ResponseBody
    public ModelAndView resetPassword(@RequestParam("email") final String userEmail,
                                      final RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {
        userService.resetPassword(userEmail, request);
        redirectAttributes.addFlashAttribute("globalMessage", "You should receive an Password Reset Email shortly.");
        return new ModelAndView("redirect:/login");
    }

    @GetMapping(value = "/user/changePassword")
    public ModelAndView showChangePasswordPage(@RequestParam("id") Long id,
                                               @RequestParam("token") final String token,
                                               final RedirectAttributes redirectAttributes) {
        PasswordResetToken passToken = tokenService.findPasswordResetToken(token);
        if (passToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }
        User user = passToken.getUser();
        if (!user.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }

        if (passToken.isExpired()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your password reset token has expired");
            return new ModelAndView("redirect:/login");
        }

        final ModelAndView view = new ModelAndView("resetPassword");
        view.addObject("questions", securityQuestionService.getQuestionDefinitions());
        view.addObject("token", token);
        return view;
    }

    //TODO: Bullshit, i cant send pass in request param. I think we need use body with https protocol.
    @PostMapping(value = "/user/savePassword")
    @ResponseBody
    public ModelAndView savePassword(@RequestParam("password") final String password,
                                     @RequestParam("passwordConfirmation") final String passwordConfirmation,
                                     @RequestParam("token") final String token,
                                     @RequestParam final Long questionId,
                                     @RequestParam final String answer,
                                     final RedirectAttributes redirectAttributes) {
        Map<String, Object> errorModel = new HashMap<>();
        if (!password.equals(passwordConfirmation)) {
            errorModel.put("errorMessage", "Passwords do not match");
            errorModel.put("questions", securityQuestionService.getQuestionDefinitions());
            return new ModelAndView("resetPassword", errorModel);
        }

        final PasswordResetToken passwordResetToken = tokenService.findPasswordResetToken(token);
        if (passwordResetToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid token");
        } else {
            final User user = passwordResetToken.getUser();
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unknown user");
            } else {
                if (!securityQuestionService.checkAnswer(questionId, user.getId(), answer)) {
                    errorModel.put("errorMessage", "Answer to security question is incorrect");
                    errorModel.put("questions", securityQuestionService.getQuestionDefinitions());
                    return new ModelAndView("resetPassword", errorModel);
                }
                userService.changeUserPassword(user, password);
                redirectAttributes.addFlashAttribute("globalMessage", "Password reset successfully");
            }
        }
        return new ModelAndView("redirect:/login");
    }
}
