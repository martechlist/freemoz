package com.freemoz.app;

import com.freemoz.app.config.Values;
import com.freemoz.app.routes.ContentRoute;
import com.freemoz.app.routes.EditorRoute;
import com.freemoz.app.routes.SearchRoute;
import com.freemoz.app.routes.SuggestRoute;
import com.freemoz.app.service.Singleton;
import com.freemoz.app.util.Helpers;
import com.freemoz.app.util.JsonTransformer;
import com.freemoz.app.util.Properties;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

/**
 * Main entry point for the application.
 */
public class App {
    public static void main( String[] args ) {
        // Database migrations happen before we start
        preStart();

        Spark.port(getServerPort());

        if (1 == 1) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public/";
            staticFiles.externalLocation(projectDir + staticDir);
        } else {
            Spark.staticFileLocation("/public");
        }


        get("/", (request, response) -> new ModelAndView(null, "index.ftl"), new FreeMarkerEngine());

        get("/login/", (EditorRoute::login), new FreeMarkerEngine());
        post("/login/", (EditorRoute::doLogin), new FreeMarkerEngine());
        get("/logout/", (EditorRoute::logout), new FreeMarkerEngine());
        get("/search/", (SearchRoute::search), new FreeMarkerEngine());

        get("/about/", (request, response) -> new ModelAndView(null, "about.ftl"), new FreeMarkerEngine());
        get("/become/", (request, response) -> new ModelAndView(null, "become.ftl"), new FreeMarkerEngine());
        get("/suggest/", (request, response) -> new ModelAndView(null, "suggest.ftl"), new FreeMarkerEngine());
        post("/suggest/", (SuggestRoute::suggest), new FreeMarkerEngine());

        // Special routes to preserve the root categories
        get("/Arts/*", (request, response) -> ContentRoute.getCategory(request, response, "Arts"), new FreeMarkerEngine());
        get("/Business/*", (request, response) -> ContentRoute.getCategory(request, response, "Business"), new FreeMarkerEngine());
        get("/Computers/*", (request, response) -> ContentRoute.getCategory(request, response, "Computers"), new FreeMarkerEngine());
        get("/Games/*", (request, response) -> ContentRoute.getCategory(request, response, "Games"), new FreeMarkerEngine());
        get("/Health/*", (request, response) -> ContentRoute.getCategory(request, response, "Health"), new FreeMarkerEngine());
        get("/Home/*", (request, response) -> ContentRoute.getCategory(request, response, "Home"), new FreeMarkerEngine());
        get("/News/*", (request, response) -> ContentRoute.getCategory(request, response, "News"), new FreeMarkerEngine());
        get("/Recreation/*", (request, response) -> ContentRoute.getCategory(request, response, "Recreation"), new FreeMarkerEngine());
        get("/Reference/*", (request, response) -> ContentRoute.getCategory(request, response, "Reference"), new FreeMarkerEngine());
        get("/Regional/*", (request, response) -> ContentRoute.getCategory(request, response, "Regional"), new FreeMarkerEngine());
        get("/Science/*", (request, response) -> ContentRoute.getCategory(request, response, "Science"), new FreeMarkerEngine());
        get("/Shopping/*", (request, response) -> ContentRoute.getCategory(request, response, "Shopping"), new FreeMarkerEngine());
        get("/Society/*", (request, response) -> ContentRoute.getCategory(request, response, "Society"), new FreeMarkerEngine());
        get("/Sports/*", (request, response) -> ContentRoute.getCategory(request, response, "Sports"), new FreeMarkerEngine());
        get("/World/*", (request, response) -> ContentRoute.getCategory(request, response, "World"), new FreeMarkerEngine());


        ////////////////////////////////////////////////////
        //              Editor Routes Below
        ////////////////////////////////////////////////////

        path("/editor", () -> {
            get("/", (EditorRoute::editorAdmin), new FreeMarkerEngine());
            get("/submissions/", (EditorRoute::editorSubmissions), new FreeMarkerEngine());
        });

        ////////////////////////////////////////////////////
        //              API Routes Below
        ////////////////////////////////////////////////////

        path("/api", () -> {
            path("/v1", () -> {
                get("/categories/", ContentRoute::getCategories, new JsonTransformer());
                get("/submission/", ContentRoute::getSubmission, new JsonTransformer());
                get("/search/", SearchRoute::searchJson, new JsonTransformer());
                get("/searchurl/", SearchRoute::searchUrlJson, new JsonTransformer());
                get("/searchdescription/", SearchRoute::searchDescriptionJson, new JsonTransformer());
            });
        });


    }

    private static int getServerPort() {
        return Helpers.tryParseInt(Properties.getProperties().getProperty(Values.SERVER_PORT, "" + Values.DEFAULT_SERVER_PORT), Values.DEFAULT_SERVER_PORT);
    }

    private static void preStart() {
        // Setup database
        Singleton.getUserDAO().createTableIfMissing();
        Singleton.getQueueDAO().createTableIfMissing();

        // Start jobs
        //Singleton.getJobService().startJobs();
    }
}
