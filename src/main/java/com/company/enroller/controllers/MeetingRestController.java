package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<Meeting>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
		if (meetingService.findById(meeting.getId()) != null) {
			ResponseEntity<Meeting> entity = new ResponseEntity<Meeting>(HttpStatus.CONFLICT);
			return entity;
		} else {
			meetingService.saveMeeting(meeting);
			return new ResponseEntity("A meeting with login " + meeting.getId() + " has been created.",
					HttpStatus.CREATED);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("A participant with login " + meeting.getTitle() + " does not exist",
					HttpStatus.NOT_FOUND);
		} else {
			meetingService.deleteMeeting(meeting);
			return new ResponseEntity("A participant with login " + meeting.getTitle() + " has been deleted.",
					HttpStatus.NO_CONTENT);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting incommingMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		} else {
			if (!incommingMeeting.getTitle().equals(null) || !incommingMeeting.getTitle().equals(meeting.getTitle())) {
				meeting.setTitle(incommingMeeting.getTitle());
			}
			if (!incommingMeeting.getDescription().equals(null) || !incommingMeeting.getDescription().equals(meeting.getDescription())) {
				meeting.setDescription(incommingMeeting.getDescription());
			}
			if (!incommingMeeting.getDate().equals(null) || !incommingMeeting.getDate().equals(meeting.getDate())) {
				meeting.setDate(incommingMeeting.getDate());
			}
			meetingService.updateMeeting(meeting);
			return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
		}
	}

}
