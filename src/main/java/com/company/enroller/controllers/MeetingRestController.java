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
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@Autowired
	ParticipantService participantService;

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
		// Ten warunek chyba jest niekonieczny (autmoatyczna generacja id)
		if (meetingService.findById(meeting.getId()) != null) {
			ResponseEntity<Meeting> entity = new ResponseEntity<Meeting>(HttpStatus.CONFLICT);
			return entity;
		} else {
			meetingService.saveMeeting(meeting);
			return new ResponseEntity(
					"A meeting entitled " + meeting.getTitle() + " on " + meeting.getDate() + " has been created.",
					HttpStatus.CREATED);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		} else {
			meetingService.deleteMeeting(meeting);
			return new ResponseEntity("A meeting  " + meeting.getTitle() + " has been deleted.", HttpStatus.NO_CONTENT);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting incommingMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		} else {
			if (incommingMeeting.getTitle() != null && !incommingMeeting.getTitle().equals("")
					&& !incommingMeeting.getTitle().equals(meeting.getTitle())) {
				meeting.setTitle(incommingMeeting.getTitle());
			}
			if (incommingMeeting.getDescription() != null && !incommingMeeting.getDescription().equals("")
					&& !incommingMeeting.getDescription().equals(meeting.getDescription())) {
				meeting.setDescription(incommingMeeting.getDescription());
			}
			if (incommingMeeting.getDate() != null && !incommingMeeting.getDate().equals("")
					&& !incommingMeeting.getDate().equals(meeting.getDate())) {
				meeting.setDate(incommingMeeting.getDate());
			}
			meetingService.updateMeeting(meeting);
			return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getAllEnrolled(@PathVariable("id") long id) {
		if (meetingService.findById(id) == null) {
			return new ResponseEntity("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		} else {
			Collection<Participant> participants = meetingService.getEnrolled(id);
			return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	public ResponseEntity<?> enrollParticipant(@PathVariable("id") long id,
			@RequestBody Participant enrollingParticipant) {
		Participant participant = participantService.findByLogin(enrollingParticipant.getLogin());
		if (participant == null) {
			return new ResponseEntity("A participant with login " + enrollingParticipant.getLogin() + " does not exist",
					HttpStatus.NOT_FOUND);
		} else {
			meetingService.enroll(id, participant);
			Collection<Participant> participants = meetingService.getEnrolled(id);
			return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.PUT)
	public ResponseEntity<?> unEnrollParticipant(@PathVariable("id") long id,
			@RequestBody Participant enrollingParticipant) {
		Participant participant = participantService.findByLogin(enrollingParticipant.getLogin());
		Collection<Participant> participants = meetingService.getEnrolled(id);
		if (participant == null) {
			return new ResponseEntity("A participant with login " + enrollingParticipant.getLogin() + " does not exist",
					HttpStatus.NOT_FOUND);
		} else if (!participants.contains(participant)) {
			return new ResponseEntity("A participant with login " + enrollingParticipant.getLogin()
					+ " has not been enrolled to this meeting", HttpStatus.NOT_FOUND);
		} else {
			meetingService.unenroll(id, participant);
			return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
		}
	}

}
