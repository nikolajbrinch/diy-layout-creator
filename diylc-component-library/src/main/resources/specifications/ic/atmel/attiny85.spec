{
	"id": "6f63a25e-d784-49e2-a362-e79819e36a19",
	"category" : "IC",
	"name" : "ATtiny85-20PU",
	"packageType" : "DIP8",
	"description" : "atmel",
	"width" : 6, 
	"pins" : {
		"left" : [
			{ 
				"id" : 8,
				"name" : "VCC"
			},
			{
				"id" : 4,
				"name" : "GND"
			}
		],
		"top" : [],
		"right" : [
			{ 
				"id" : 1,
				"name" : "(RESET) PB5",
				"alternatives" : [ "PCINT5", "ADC0", "dW" ]
			},
			{
				"id" : 3,
				"name" : "(XTAL2) PB4",
				"alternatives" : [ "PCINT4", "CLKO", "OC1B", "ADC2" ]
			},
			{ 
				"id" : 2,
				"name" : "(XTAL1) PB3",
				"alternatives" : [ "PCINT3", "CLKI", "OC1B", "ADC3" ]
			},
			{
				"id" : 7,
				"name" : "(T0) PB2",
				"alternatives" : [ "SCK", "USCK", "SCL", "ADC1", "INT0", "PCINT2" ]
			},
			{ 
				"id" : 6,
				"name" : "(AIN1) PB1",
				"alternatives" : [ "MISO", "DO", "OC0B", "OC1A", "PCINT1" ]
			},
			{
				"id" : 5,
				"name" : "(AIN0) PB0",
				"alternatives" : [ "MOSI", "DI", "SDA", "OC0A", "OC1A", "AREF", "PCINT0" ]
			}
		],			
		"bottom" : []
	}
}	