package app.Controller;

import app.config.HibernateConfig;
import app.daos.HotelDAO;
import app.dtos.HotelDTO;
import app.services.HotelService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelController {

    // Logger til generel logging
    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

    // Logger til debug-specifik information (kan bruges til tracing)
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    // DAO til at håndtere CRUD-operationer mod database
    private static final HotelService hotelService = new HotelService(new HotelDAO(HibernateConfig.getEntityManagerFactory()));

    // ========================
    // CRUD: HOTEL ENDPOINTS
    // ========================

    /**
     * GET /hotel
     * Hent alle hoteller
     */
    public static void getAllHotels(Context ctx) {
        ctx.json(hotelService.getAllHotels()).status(200); // HTTP 200 OK
    }

    /**
     * GET /hotel/{id}
     * Hent et specifikt hotel efter ID
     */
    public static void getHotelById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("hotelId"));
        ctx.json(hotelService.getHotelById(id)).status(200); // HTTP 200 OK
    }

    /**
     * POST /hotel
     * Opret nyt hotel
     */
    public static void createHotel(Context ctx) {
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        HotelDTO createdHotel = hotelService.createHotel(hotelDTO);
        ctx.status(201).json(createdHotel);
    }

    /**
     * PUT /hotel/{id}
     * Opdater eksisterende hotel
     */
    public static void updateHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("hotelId"));
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        ctx.json(hotelService.updateHotel(id, hotelDTO)).status(202); // HTTP 202 Accepted
    }

    /**
     * DELETE /hotel/{id}
     * Slet hotel
     */
    public static void deleteHotel(Context ctx) {
      int id = Integer.parseInt(ctx.pathParam("hotelId"));
      boolean deleted = hotelService.deleteHotel(id);
      if(!deleted) {
          ctx.status(404).result("Hotel with id " + id + " not found");
      } else {
          ctx.status(202).result("Hotel with id " + id + " deleted");
      }
    }

    // ========================
    // ROOMS ENDPOINTS
    // ========================

    /**
     * GET /hotel/{id}/rooms
     * Hent alle værelser for et specifikt hotel
     */

    public static void getRoomsForHotel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("hotelId"));
        ctx.json(hotelService.getRoomsForHotel(id));
    }
}
