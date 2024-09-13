package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Event;
import com.example.demo.repository.EventRepository;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public List<Event> getEvents(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return eventRepository.findByStartBetween(startDate, endDate);
    }


    @PostMapping
    public String createEvent(@RequestBody Event event) {
        // Validate input
        if (event.getStart() == null || event.getEnd() == null) {
            return "Start and end dates must not be null.";
        }

        // Check if the start time is before the end time
        if (event.getStart().isAfter(event.getEnd())) {
            return "Start time must be before end time.";
        }

        // Check for existing events that overlap with the requested booking
        List<Event> existingEvents = eventRepository.findByStartBetween(event.getStart(), event.getEnd());
        if (!existingEvents.isEmpty()) {
            return "Room is already booked during this time.";
        }

        eventRepository.save(event);
        return "Booking confirmed.";
    }

}