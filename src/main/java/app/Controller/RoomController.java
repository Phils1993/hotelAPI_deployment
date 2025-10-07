package app.Controller;


import app.daos.HotelDAO;
import app.dtos.RoomDTO;

import app.exceptions.ApiException;
import app.services.RoomService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class RoomController {

    // Logger til generel logging
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    // Logger til debug/tracing
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    // DAO til at håndtere hotel- og room-CRUD
    private static final RoomService roomService = new RoomService(
            new HotelDAO(app.config.HibernateConfig.getEntityManagerFactory()));


    // ========================
    // CRUD: ROOM ENDPOINTS
    // ========================

    /**
     * GET /hotel/{hotelId}/rooms
     *      * Hent alle værelser for et specifikt hotel
     */
    public static void getRoomsForHotel(Context ctx) {
        int hotelId = Integer.parseInt(ctx.pathParam("hotelId"));
        List<RoomDTO> rooms = roomService.getRoomsForHotel(hotelId);
        ctx.json(rooms);
    }

    /**
     * GET /room/{id}
     * Hent et specifikt værelse efter ID
     */
    public static void getRoomById(Context ctx) {
        int roomId = Integer.parseInt(ctx.pathParam("roomId"));
        RoomDTO roomDTO = roomService.getRoomById(roomId);
        ctx.json(roomDTO);
    }

    /**
     * PUT /hotel/{hotelId}/rooms/{roomId}
     * Opdater et værelse
     */
    public static void updateRoom(Context ctx) {
        int hotelId = Integer.parseInt(ctx.pathParam("hotelId"));
        int roomId = Integer.parseInt(ctx.pathParam("roomId"));
        RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
        RoomDTO updated = roomService.updateRoom(hotelId, roomId, roomDTO);
        ctx.json(updated);
    }

    /**
     * POST /hotel/{hotelId}/rooms
     * Tilføj et nyt værelse til et hotel
     */
    public static void addRoom(Context ctx) {
        int hotelId = Integer.parseInt(ctx.pathParam("hotelId"));
        RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
        RoomDTO created = roomService.addRoom(hotelId, roomDTO);
        ctx.status(201).json(created);
    }

    /**
     * DELETE /hotel/{hotelId}/rooms/{roomId}
     * Slet et værelse fra et hotel
     */
    public static void deleteRoom(Context ctx) {
        int hotelId = Integer.parseInt(ctx.pathParam("hotelId"));
        int roomId = Integer.parseInt(ctx.pathParam("roomId"));
        boolean removed = roomService.removeRoom(hotelId, roomId);
        if (removed) {
            ctx.status(202).result("Room removed successfully");
        } else {
            throw new ApiException(404, "Room not found");
        }
    }
}
