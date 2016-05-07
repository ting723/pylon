package com.yotouch.base.web.authrize;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.base.service.PaginationService;
import com.yotouch.base.web.BaseController;

@Controller
public class MenuController extends BaseController{

    @Autowired
    PaginationService paginationService;

    @RequestMapping("/admin/menu/list")
    public String list(
            @RequestParam(name = "currentPage", defaultValue = "1") int currentPage,
            Model model
    ) {
        DbSession dbSession = this.getDbSession();

        int itemPerPage = Consts.itemPerPage;
        List<Entity> menus = dbSession.queryRawSql("menu", "status = ?", new Object[]{Consts.STATUS_NORMAL});

        int total = menus.size();

        if (total > 0) {
            Object[] queryCondition = {Consts.STATUS_NORMAL};
            String queryString = "status = ? ORDER BY createdAt DESC ";
            Map<String, Object> paginationInfo = paginationService.getPaginationInfo(currentPage, total, itemPerPage, "menu", queryString, queryCondition, "/admin/menu/list?currentPage=");
            model.addAttribute("paginationInfo", paginationInfo.get("paginationInfo"));
            menus = (List<Entity>) paginationInfo.get("menu");
            model.addAttribute("menus", menus);
        } else {
            model.addAttribute("menus", menus);
        }

        model.addAttribute("menus", menus);

        return "/admin/menu/list";
    }

    @RequestMapping("/admin/menu/edit")
    public String edit(
            @RequestParam(name = "uuid", defaultValue = "") String uuid,
            Model model
    ) {
        DbSession dbSession = this.getDbSession();
        Entity menu = dbSession.getEntity("menu", uuid);

        model.addAttribute("menu", menu);

        List<Entity> topmenus = dbSession.queryRawSql("menu", "parentUuid = ? AND status = ?", new Object[]{"", Consts.STATUS_NORMAL});
        model.addAttribute("topmenus", topmenus);

        return "/admin/menu/edit";
    }

    @RequestMapping(value="/admin/menu/edit", method=RequestMethod.POST)
    public String doedit(
            @RequestParam(value="uuid", defaultValue="") String uuid,
            @RequestParam("parentUuid") String parentUuid,
            HttpServletRequest request,
            Model model
    ) {


        DbSession dbSession = this.getDbSession();
        Entity menu = dbSession.getEntity("menu", uuid);
        if (menu == null) {
            menu = dbSession.newEntity("menu", Consts.STATUS_NORMAL);
        }

        menu = webUtil.updateEntityVariables(menu, request);
        menu.setValue("parent", parentUuid);

        dbSession.save(menu);

        return "redirect:/admin/menu/list";
    }

}
