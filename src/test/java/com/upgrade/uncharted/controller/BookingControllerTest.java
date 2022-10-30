package com.upgrade.uncharted.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.uncharted.dto.BookingRequest;
import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;
import com.upgrade.uncharted.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper om = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void testDeleteBooking_whenBookingDoesntExist() throws Exception {

        Mockito.when(bookingService.deleteBooking(anyString())).thenReturn(0);

        mockMvc.perform(MockMvcRequestBuilders.delete("/booking/{bookingIdentifier}", "aaaa")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteBooking_whenBookingDoesExist() throws Exception {

        Mockito.when(bookingService.deleteBooking(anyString())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/booking/{bookingIdentifier}", "aaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAvailabilities() throws Exception {

        Mockito.when(bookingService.getAvailabilities(any(LocalDate.class), any(LocalDate.class))).thenReturn(new HashSet<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/booking/availabilities")
                        .param("from", LocalDate.now().plusDays(1).toString())
                        .param("to", LocalDate.now().plusDays(3).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testBook() throws Exception {

        Mockito.when(bookingService.book(any(BookingRequest.class))).thenReturn(new BookingResponse("aaaa"));

        BookingRequest request = BookingRequest.builder()
                        .arrivalDate(LocalDate.now().plusDays(1).toString())
                        .departureDate(LocalDate.now().plusDays(2).toString())
                        .build();

        String requestJson = om.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'bookingIdentifier': 'aaaa'}"));
    }

    @Test
    public void testUpdateBooking() throws Exception {

        Mockito.when(bookingService.updateBooking(anyString(), any(UpdateBookingRequest.class)))
                .thenReturn(new BookingResponse("aaaa"));

        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(1).toString())
                .departureDate(LocalDate.now().plusDays(2).toString())
                .build();

        String requestJson = om.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/booking/{bookingIdentifier}", "aaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{'bookingIdentifier': 'aaaa'}"));
    }




}
