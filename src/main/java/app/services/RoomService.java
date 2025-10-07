package app.services;

import app.daos.HotelDAO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.ApiException;
import app.mapper.RoomMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RoomService {

    private final HotelDAO hotelDAO;

    public RoomService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    public RoomDTO addRoom(int hotelId, RoomDTO roomDTO) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) throw new ApiException(404, "Hotel not found");

        // Mapper DTO til entity
        Room room = RoomMapper.toEntity(roomDTO);

        // Sæt hotel-relationen korrekt
        hotel.addRoom(room);

        // Gem hotel (cascade vil gemme room)
        hotelDAO.addRoom(hotel,room);

        return RoomMapper.toDTO(room);
    }
    public RoomDTO updateRoom(int hotelId, int roomId, RoomDTO roomDTO) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);

        Room room = hotel.getRooms().stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Room not found"));

        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setPrice(roomDTO.getPrice());

        hotelDAO.updateRoom(hotel, room);
        return new RoomDTO(room);
    }

    public boolean removeRoom(int hotelId, int roomId) {
        return hotelDAO.removeRoom(hotelId, roomId);
    }

    public List<RoomDTO> getRoomsForHotel(int hotelId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        return hotel.getRooms().stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(int roomId) {
        Room room = hotelDAO.getRoomById(roomId);
        return new RoomDTO(room);
    }
}
