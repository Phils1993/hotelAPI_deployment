
package app.daos;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HotelDAOTest {

    private static EntityManagerFactory emf;
    private static HotelDAO hotelDAO;

    private static int createdHotelId;
    private static int createdRoomId;

    @BeforeAll
    static void setupAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        hotelDAO = new HotelDAO(emf);

        // TRUNCATE tables før tests
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE room RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE hotel RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null; // so it can be re-created by next test class
        HibernateConfig.setTest(false);
    }

    @Test
    @Order(1)
    void createHotel() {
        Hotel hotel = new Hotel();
        hotel.setHotelName("Test Hotel");
        hotel.setHotelAddress("Test Street 123");
        hotel.setHotelRooms(0); // hotelRooms er nu double, ikke List

        Hotel created = hotelDAO.createHotel(hotel);
        assertNotNull(created);
        assertTrue(created.getHotelId() > 0);

        createdHotelId = created.getHotelId();
    }

    @Test
    @Order(2)
    void getAllHotels() {
        List<Hotel> hotels = hotelDAO.getAllHotels();
        assertFalse(hotels.isEmpty());
    }

    @Test
    @Order(3)
    void getHotelById() {
        Hotel hotel = hotelDAO.getHotelById(createdHotelId);
        assertNotNull(hotel);
        assertEquals("Test Hotel", hotel.getHotelName());
    }

    @Test
    @Order(4)
    void updateHotel() {
        Hotel hotel = hotelDAO.getHotelById(createdHotelId);
        hotel.setHotelName("Updated Hotel");
        hotel.setHotelAddress("Updated Street 456");

        Hotel updated = hotelDAO.updateHotel(hotel);
        assertEquals("Updated Hotel", updated.getHotelName());
        assertEquals("Updated Street 456", updated.getHotelAddress());
    }

    @Test
    @Order(5)
    void addRoom() {
        Hotel hotel = hotelDAO.getHotelById(createdHotelId);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setPrice(200.0);

        Hotel updatedHotel = hotelDAO.addRoom(hotel, room);
        assertNotNull(updatedHotel.getRooms());
        assertFalse(updatedHotel.getRooms().isEmpty());

        // Få det første element fra set
        createdRoomId = updatedHotel.getRooms().iterator().next().getId();
    }

    @Test
    @Order(6)
    void getRoomById() {
        Room room = hotelDAO.getRoomById(createdRoomId);
        assertNotNull(room);
        assertEquals(101, room.getRoomNumber());
    }

    @Test
    @Order(7)
    void updateRoom() {
        Hotel hotel = hotelDAO.getHotelById(createdHotelId);
        Room room = hotelDAO.getRoomById(createdRoomId);

        room.setRoomNumber(102);
        room.setPrice(250.0);

        Room updated = hotelDAO.updateRoom(hotel, room);
        assertEquals(102, updated.getRoomNumber());
        assertEquals(250.0, updated.getPrice());
    }

    @Test
    @Order(8)
    void getRoomsForHotel() {
        Hotel hotel = hotelDAO.getHotelById(createdHotelId);
        List<Room> rooms = hotelDAO.getRoomsForHotel(hotel);
        assertEquals(1, rooms.size());
    }

    @Test
    @Order(9)
    void removeRoom() {
        boolean removed = hotelDAO.removeRoom(createdHotelId, createdRoomId);
        assertTrue(removed);

        Hotel hotel = hotelDAO.getHotelById(createdHotelId);
        assertTrue(hotel.getRooms().isEmpty());
    }

    @Test
    @Order(10)
    void deleteHotel() {
        boolean deleted = hotelDAO.deleteHotel(createdHotelId);
        assertTrue(deleted);

        try {
            Hotel hotel = hotelDAO.getHotelById(createdHotelId);
            fail("Hotel should have been deleted");
        } catch (ApiException e) {
            assertEquals("Hotel not found", e.getMessage());
        }
    }
}

