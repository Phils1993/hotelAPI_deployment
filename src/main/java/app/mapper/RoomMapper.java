package app.mapper;

import app.dtos.RoomDTO;
import app.entities.Room;

public class RoomMapper {

    // Konverter Room entity til DTO
    public static RoomDTO toDTO(Room room) {
        if (room == null) return null;
        return RoomDTO.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .price(room.getPrice())
                .hotelId(room.getHotel() != null ? room.getHotel().getHotelId() : null)
                .build();
    }

    // Konverter DTO til Room entity
    public static Room toEntity(RoomDTO dto) {
        if (dto == null) return null;
        return Room.builder()
                .id(dto.getId())
                .roomNumber(dto.getRoomNumber())
                .price(dto.getPrice())
                // hotel sættes IKKE her, det gøres via hotel.addRoom()
                .build();
    }
}
