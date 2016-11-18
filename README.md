# SOEN423_Project
Distributed Flight Reservation System  
Software Failure Tolerant & Highly Available

## IDL Format  
For any method, strings could be lowercase, uppercase, mix of both, etc. Make no assumptions.

	interface FlightReservationServer{
		string bookFlight(in string firstName, in string lastName, in string address, in
		string phoneNumber, in string destination, in string date, in string flightClass);
		firstName: "John" (any string)
		lastName: "Doe" (any string)
		address: "Address" (any string)
		phoneNumber: "514-123-4567" (any string)
		destination: "MTL", "WST", "NDL"
		date: "mm/dd/yyyy" (american simple date format)
		flightClass: "ECONOMY", "BUSINESS", "FIRST"
		
		string getBookedFlightCount(in string recordType);	
		recordType: "MTL1111|FIRST" (managerId | flightClass)
		
		string editFlightRecord(in string recordId, in string fieldName, in string newValue);
		recordId: "MTL1111|5" (managerId | flightRecordId)
		fieldName: "CREATE", "DELETE", "EDIT|DESTINATION", "EDIT|DATE", "EDIT|ECONOMY",
		"EDIT|BUSINESS", "EDIT|FIRST" (for edits, operation | fieldName, otherwise single
		token)
		
		CREATE
		newValue : "MTL|11/15/2016|15|10|5" (destination | date | economy | business | first)
		
		DELETE
		newValue : ""
		
		EDIT|DESTINATION
		newValue "WST"
		
		EDIT|DATE
		newValue: "11/15/2016"
		
		EDIT|ECONOMY
		newValue: "15"
		
		EDIT|BUSINESS
		newValue: "10"
		
		EDIT|FIRST
		newValue: "5"
		
		*flightRecordId ignored when performing CREATE
		*newValue ignored when performing DELETE
		
		string transferReservation(in string passengerId, in string currentCity, in string
		otherCity);
		passengerId: "MTL1111|5" (managerId | passengerRecordId)
		currentCity: "MTL", "WST", "NDL"
		otherCity: "MTL", "WST", "NDL"
		
		strings getFlights();
		return String[];
		format TBD
		
		strings getReservations();
		return String[];
		format TBD
	};

