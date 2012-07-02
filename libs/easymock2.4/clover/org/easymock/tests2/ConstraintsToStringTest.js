var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = { "classes" : [
    {"id" : 2859, "sl" : 17, "el" : 147, "name" : "ConstraintsToStringTest",
    "methods" : [
              {"sl" : 20, "el" : 23, "sc" : 5},  {"sl" : 25, "el" : 30, "sc" : 5},  {"sl" : 32, "el" : 36, "sc" : 5},  {"sl" : 38, "el" : 42, "sc" : 5},  {"sl" : 44, "el" : 48, "sc" : 5},  {"sl" : 50, "el" : 54, "sc" : 5},  {"sl" : 56, "el" : 66, "sc" : 5},  {"sl" : 59, "el" : 62, "sc" : 13},  {"sl" : 68, "el" : 73, "sc" : 5},  {"sl" : 75, "el" : 79, "sc" : 5},  {"sl" : 81, "el" : 91, "sc" : 5},  {"sl" : 84, "el" : 87, "sc" : 13},  {"sl" : 93, "el" : 100, "sc" : 5},  {"sl" : 102, "el" : 106, "sc" : 5},  {"sl" : 108, "el" : 115, "sc" : 5},  {"sl" : 117, "el" : 121, "sc" : 5},  {"sl" : 123, "el" : 127, "sc" : 5},  {"sl" : 129, "el" : 133, "sc" : 5},  {"sl" : 135, "el" : 139, "sc" : 5},  {"sl" : 141, "el" : 145, "sc" : 5}  ]}
    
 ]
};

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {
		"test_434" : {
					  "name" : "findToString",
					  "pass" : true ,
					  "methods" : [{"sl": 135 },],
					  "statements" : [{"sl": 137 },{"sl": 138 },]
					  },
		"test_415" : {
					  "name" : "sameToStringWithObject",
					  "pass" : true ,
					  "methods" : [{"sl": 56 },{"sl": 59 },],
					  "statements" : [{"sl": 58 },{"sl": 61 },{"sl": 64 },{"sl": 65 },]
					  },
		"test_406" : {
					  "name" : "sameToStringWithString",
					  "pass" : true ,
					  "methods" : [{"sl": 25 },],
					  "statements" : [{"sl": 27 },{"sl": 28 },]
					  },
		"test_458" : {
					  "name" : "startsWithToString",
					  "pass" : true ,
					  "methods" : [{"sl": 117 },],
					  "statements" : [{"sl": 119 },{"sl": 120 },]
					  },
		"test_442" : {
					  "name" : "matchesToString",
					  "pass" : true ,
					  "methods" : [{"sl": 141 },],
					  "statements" : [{"sl": 143 },{"sl": 144 },]
					  },
		"test_407" : {
					  "name" : "notNullToString",
					  "pass" : true ,
					  "methods" : [{"sl": 38 },],
					  "statements" : [{"sl": 40 },{"sl": 41 },]
					  },
		"test_437" : {
					  "name" : "containsToString",
					  "pass" : true ,
					  "methods" : [{"sl": 129 },],
					  "statements" : [{"sl": 131 },{"sl": 132 },]
					  },
		"test_451" : {
					  "name" : "orToString",
					  "pass" : true ,
					  "methods" : [{"sl": 93 },],
					  "statements" : [{"sl": 95 },{"sl": 96 },{"sl": 97 },{"sl": 98 },{"sl": 99 },]
					  },
		"test_438" : {
					  "name" : "endsWithToString",
					  "pass" : true ,
					  "methods" : [{"sl": 123 },],
					  "statements" : [{"sl": 125 },{"sl": 126 },]
					  },
		"test_411" : {
					  "name" : "anyToString",
					  "pass" : true ,
					  "methods" : [{"sl": 44 },],
					  "statements" : [{"sl": 46 },{"sl": 47 },]
					  },
		"test_454" : {
					  "name" : "equalsToStringWithObject",
					  "pass" : true ,
					  "methods" : [{"sl": 81 },{"sl": 84 },],
					  "statements" : [{"sl": 83 },{"sl": 86 },{"sl": 89 },{"sl": 90 },]
					  },
		"test_409" : {
					  "name" : "nullToString",
					  "pass" : true ,
					  "methods" : [{"sl": 32 },],
					  "statements" : [{"sl": 34 },{"sl": 35 },]
					  },
		"test_414" : {
					  "name" : "sameToStringWithChar",
					  "pass" : true ,
					  "methods" : [{"sl": 50 },],
					  "statements" : [{"sl": 52 },{"sl": 53 },]
					  },
		"test_447" : {
					  "name" : "equalsToStringWithChar",
					  "pass" : true ,
					  "methods" : [{"sl": 75 },],
					  "statements" : [{"sl": 77 },{"sl": 78 },]
					  },
		"test_450" : {
					  "name" : "equalsToStringWithString",
					  "pass" : true ,
					  "methods" : [{"sl": 68 },],
					  "statements" : [{"sl": 70 },{"sl": 71 },]
					  },
		"test_456" : {
					  "name" : "notToString",
					  "pass" : true ,
					  "methods" : [{"sl": 102 },],
					  "statements" : [{"sl": 104 },{"sl": 105 },]
					  },
		"test_457" : {
					  "name" : "andToString",
					  "pass" : true ,
					  "methods" : [{"sl": 108 },],
					  "statements" : [{"sl": 110 },{"sl": 111 },{"sl": 112 },{"sl": 113 },{"sl": 114 },]
					  }
 };

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [  [],   [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 406   ] ,
  [  ] ,
  [ 406   ] ,
  [ 406   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 409   ] ,
  [  ] ,
  [ 409   ] ,
  [ 409   ] ,
  [  ] ,
  [  ] ,
  [ 407   ] ,
  [  ] ,
  [ 407   ] ,
  [ 407   ] ,
  [  ] ,
  [  ] ,
  [ 411   ] ,
  [  ] ,
  [ 411   ] ,
  [ 411   ] ,
  [  ] ,
  [  ] ,
  [ 414   ] ,
  [  ] ,
  [ 414   ] ,
  [ 414   ] ,
  [  ] ,
  [  ] ,
  [ 415   ] ,
  [  ] ,
  [ 415   ] ,
  [ 415   ] ,
  [  ] ,
  [ 415   ] ,
  [  ] ,
  [  ] ,
  [ 415   ] ,
  [ 415   ] ,
  [  ] ,
  [  ] ,
  [ 450   ] ,
  [  ] ,
  [ 450   ] ,
  [ 450   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 447   ] ,
  [  ] ,
  [ 447   ] ,
  [ 447   ] ,
  [  ] ,
  [  ] ,
  [ 454   ] ,
  [  ] ,
  [ 454   ] ,
  [ 454   ] ,
  [  ] ,
  [ 454   ] ,
  [  ] ,
  [  ] ,
  [ 454   ] ,
  [ 454   ] ,
  [  ] ,
  [  ] ,
  [ 451   ] ,
  [  ] ,
  [ 451   ] ,
  [ 451   ] ,
  [ 451   ] ,
  [ 451   ] ,
  [ 451   ] ,
  [  ] ,
  [  ] ,
  [ 456   ] ,
  [  ] ,
  [ 456   ] ,
  [ 456   ] ,
  [  ] ,
  [  ] ,
  [ 457   ] ,
  [  ] ,
  [ 457   ] ,
  [ 457   ] ,
  [ 457   ] ,
  [ 457   ] ,
  [ 457   ] ,
  [  ] ,
  [  ] ,
  [ 458   ] ,
  [  ] ,
  [ 458   ] ,
  [ 458   ] ,
  [  ] ,
  [  ] ,
  [ 438   ] ,
  [  ] ,
  [ 438   ] ,
  [ 438   ] ,
  [  ] ,
  [  ] ,
  [ 437   ] ,
  [  ] ,
  [ 437   ] ,
  [ 437   ] ,
  [  ] ,
  [  ] ,
  [ 434   ] ,
  [  ] ,
  [ 434   ] ,
  [ 434   ] ,
  [  ] ,
  [  ] ,
  [ 442   ] ,
  [  ] ,
  [ 442   ] ,
  [ 442   ] ,
  [  ] ,
  [  ] ,
  [  ] 
];
