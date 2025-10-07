package app.services;

import app.daos.HotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.ApiException;
import app.mapper.HotelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class HotelService {

    private final HotelDAO hotelDAO;
    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    public List<HotelDTO> getAllHotels() {
        return hotelDAO.getAllHotels()
                .stream()
                .map(HotelMapper::toDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO getHotelById(int id) {
        Hotel hotel = hotelDAO.getHotelById(id);
        if (hotel == null) throw new ApiException(404,"Hotel not found");
        return HotelMapper.toDTO(hotel);
    }

    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = HotelMapper.toEntity(hotelDTO);
        Hotel createdHotel = hotelDAO.createHotel(hotel);
        return HotelMapper.toDTO(createdHotel);
    }

    public HotelDTO updateHotel(int id, HotelDTO hotelDTO) {
        Hotel hotel = HotelMapper.toEntity(hotelDTO);
        hotel.setHotelId(id);
        Hotel updatedHotel = hotelDAO.updateHotel(hotel);
        return HotelMapper.toDTO(updatedHotel);
    }

    public boolean deleteHotel(int id) {
        return hotelDAO.deleteHotel(id);
    }

    // ========= Rooms =========

    public List<RoomDTO> getRoomsForHotel(int hotelId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) throw new ApiException(404, "Hotel not found");

        return hotelDAO.getRoomsForHotel(hotel)
                .stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    public RoomDTO addRoom(int hotelId, RoomDTO roomDTO) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) throw new ApiException(404, "Hotel not found");

        Room room = new Room();
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setPrice(roomDTO.getPrice());

        // Tilføj room til hotel
        hotel.getRooms().add(room);
        room.setHotel(hotel);

        // Gem ændringen via DAO (updateHotel)
        hotelDAO.updateHotel(hotel);

        return new RoomDTO(room);
    }

    public boolean removeRoom(int hotelId, int roomId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) throw new ApiException(404, "Hotel not found");

        Room room = hotel.getRooms()
                .stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Room not found"));

        hotel.getRooms().remove(room);

        hotelDAO.updateHotel(hotel);

        return true;
    }

    public RoomDTO updateRoom(int hotelId, int roomId, RoomDTO roomDTO) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) throw new ApiException(404, "Hotel not found");

        Room room = hotel.getRooms()
                .stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Room not found"));

        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setPrice(roomDTO.getPrice());

        // Gem hotel med opdateret room
        hotelDAO.updateHotel(hotel);

        return new RoomDTO(room);
    }
}
