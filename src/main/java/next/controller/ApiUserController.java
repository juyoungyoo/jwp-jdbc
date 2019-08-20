package next.controller;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.db.DataBase;
import core.mvc.JsonUtils;
import core.mvc.JsonView;
import core.mvc.ModelAndView;
import next.dto.UserCreatedDto;
import next.dto.UserUpdatedDto;
import next.model.User;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Controller
public class ApiUserController {

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView(new JsonView());

        User user = DataBase.findUserById(request.getParameter("userId"));

        return modelAndView.addObject("user", user);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserCreatedDto userCreatedDto = parseBody(request, UserCreatedDto.class);

        DataBase.addUser(userCreatedDto.toEntity());

        response.setStatus(SC_CREATED);
        response.setHeader(HttpHeaders.LOCATION, "/api/users?userId=" + userCreatedDto.getUserId());
        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.PUT)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        UserUpdatedDto updatedDto = parseBody(request, UserUpdatedDto.class);

        User existUser = DataBase.findUserById(userId);
        existUser.update(updatedDto.toEntity());

        response.setStatus(SC_OK);
        return new ModelAndView(new JsonView());
    }

    private <T> T parseBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String string = IOUtils.toString(request.getReader());
        return JsonUtils.toObject(string, clazz);
    }
}
