package app.daos;


import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;


public class HotelDAO implements IHotelDAO {

    private final EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // ================= Hotel CRUD =================

    @Override
    public List<Hotel> getAllHotels() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms", Hotel.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "No hotels found: " + e.getMessage());
        }
    }

    @Override
    public Hotel getHotelById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel == null) throw new ApiException(404, "Hotel not found");
            return hotel;
        }
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        } catch (Exception e) {
            throw new ApiException(500, "Error creating hotel: " + e.getMessage());
        }
    }

    @Override
    public Hotel updateHotel(Hotel incomingHotel) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel managed = em.find(Hotel.class, incomingHotel.getHotelId());
            if (managed == null) throw new ApiException(404, "Hotel not found");

            managed.setHotelName(incomingHotel.getHotelName());
            managed.setHotelAddress(incomingHotel.getHotelAddress());
            // Antal værelser opdateres via addRoom/removeRoom
            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating hotel: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteHotel(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel == null) return false;
            em.remove(hotel); // cascade delete rooms
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting hotel: " + e.getMessage());
        }
    }

    // ================= Room CRUD via Hotel =================

    @Override
    public Hotel addRoom(Hotel hotel, Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel managedHotel = em.find(Hotel.class, hotel.getHotelId());
            if (managedHotel == null) throw new ApiException(404, "Hotel not found");

            room.setHotel(managedHotel);
            managedHotel.getRooms().add(room);
            em.persist(room);

            em.getTransaction().commit();
            return managedHotel;
        } catch (Exception e) {
            throw new ApiException(500, "Error adding room: " + e.getMessage());
        }
    }

    @Override
    public boolean removeRoom(int hotelId, int roomId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel managedHotel = em.find(Hotel.class, hotelId);
            Room managedRoom = em.find(Room.class, roomId);

            if (managedHotel == null || managedRoom == null
                    || !managedHotel.getRooms().contains(managedRoom)) {
                return false;
            }

            managedHotel.getRooms().remove(managedRoom);
            em.remove(managedRoom);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error removing room: " + e.getMessage());
        }
    }

    @Override
    public Room updateRoom(Hotel hotel, Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Room managed = em.find(Room.class, room.getId());
            if (managed == null || managed.getHotel().getHotelId() != hotel.getHotelId()) {
                throw new ApiException(404, "Room not found for this hotel");
            }

            managed.setRoomNumber(room.getRoomNumber());
            managed.setPrice(room.getPrice());

            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating room: " + e.getMessage());
        }
    }

    @Override
    public Room getRoomById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Room room = em.find(Room.class, id);
            if (room == null) throw new ApiException(404, "Room not found");
            return room;
        }
    }

    @Override
    public List<Room> getRoomsForHotel(Hotel hotel) {
        return new ArrayList<>(hotel.getRooms());
    }
}
