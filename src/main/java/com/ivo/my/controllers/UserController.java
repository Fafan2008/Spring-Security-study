package com.ivo.my.controllers;

import com.ivo.my.configs.security.ActiveUserService;
import com.ivo.my.models.entities.User;
import com.ivo.my.services.UserService;
import com.ivo.my.utils.validations.errors.EmailExistsException;
import com.ivo.my.utils.validations.errors.LoginNameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActiveUserService activeUserService;

    @GetMapping
    public String getDefaultPage(@ModelAttribute User user) {
        return "defaultPage";
    }

    @RequestMapping("{id}")
    public ModelAndView userPage(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return new ModelAndView("users/userPage", "user", user);
    }

    @RequestMapping(value = "delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ModelAndView("redirect:/list");
    }

    @GetMapping(value = "modify/{id}")
    public ModelAndView modifyForm(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return new ModelAndView("users/modifyUserForm",
                "user",
                user);
    }

    @GetMapping(params = "form")
    public String getСreateUserForm(@ModelAttribute User user) {
        return "users/createUserForm";
    }

    //TODO: Need to fix.
    @PostMapping(params = "form")
    public ModelAndView processСreateUserForm( @Valid @ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("users/createUserForm", "formErrors", bindingResult.getAllErrors());
        }
        try {
            if (user.getId() == null) {
                //user = userService.registerNewUser(user);
                redirectAttributes.addFlashAttribute("globalMessage", "Successfully created a new user");
            } else {
                user = userService.updateExistingUser(user);
                redirectAttributes.addFlashAttribute("globalMessage", "Successfully updated the user");
            }
        } catch (EmailExistsException e) {
            bindingResult.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("users/createUserForm", "user", user);
        }
        catch (LoginNameExistsException e) {
            bindingResult.addError(new FieldError("user", "loginName", e.getMessage()));
            return new ModelAndView("users/createUserForm", "user", user);
        }
        return new ModelAndView("redirect:/{user.id}", "user.id", user.getId());
    }

    @GetMapping("/allusers")
    public String getUserList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/allUserList";
    }

    @GetMapping("/activeusers")
    public String getActiveUserList(Model model) {
        final List<User> users = activeUserService.getActiveUsers()
                .stream()
                .map(s -> userService.findUserByLoginName(s))
                .collect(Collectors.toList());
        model.addAttribute("activeUsers", users);
        return "users/activeUserList";
    }
}
