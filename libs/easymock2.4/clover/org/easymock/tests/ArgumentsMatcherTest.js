var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = { "classes" : [
    {"id" : 423, "sl" : 16, "el" : 143, "name" : "ArgumentsMatcherTest",
    "methods" : [
              {"sl" : 22, "el" : 26, "sc" : 5},  {"sl" : 28, "el" : 49, "sc" : 5},  {"sl" : 35, "el" : 42, "sc" : 13},  {"sl" : 51, "el" : 76, "sc" : 5},  {"sl" : 58, "el" : 63, "sc" : 13},  {"sl" : 78, "el" : 103, "sc" : 5},  {"sl" : 82, "el" : 84, "sc" : 13},  {"sl" : 86, "el" : 88, "sc" : 13},  {"sl" : 105, "el" : 117, "sc" : 5},  {"sl" : 119, "el" : 133, "sc" : 5},  {"sl" : 125, "el" : 127, "sc" : 13},  {"sl" : 135, "el" : 141, "sc" : 5}  ]}
    
 ]
};

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {
		"test_233" : {
					  "name" : "expectedArgumentsDelegatedToMatcher",
					  "pass" : true ,
					  "methods" : [{"sl": 28 },{"sl": 35 },],
					  "statements" : [{"sl": 30 },{"sl": 31 },{"sl": 37 },{"sl": 38 },{"sl": 39 },{"sl": 40 },{"sl": 41 },{"sl": 44 },{"sl": 45 },{"sl": 46 },{"sl": 47 },{"sl": 48 },]
					  },
		"test_257" : {
					  "name" : "abstractMatcherToStringHandlesNullArray",
					  "pass" : true ,
					  "methods" : [{"sl": 135 },],
					  "statements" : [{"sl": 137 },{"sl": 140 },]
					  },
		"test_237" : {
					  "name" : "expectedArgumentsDelegatedToMatcher2",
					  "pass" : true ,
					  "methods" : [{"sl": 51 },{"sl": 58 },],
					  "statements" : [{"sl": 53 },{"sl": 54 },{"sl": 60 },{"sl": 61 },{"sl": 62 },{"sl": 65 },{"sl": 66 },{"sl": 67 },{"sl": 68 },{"sl": 69 },{"sl": 71 },{"sl": 72 },{"sl": 73 },{"sl": 74 },{"sl": 75 },]
					  },
		"test_260" : {
					  "name" : "abstractMatcher",
					  "pass" : true ,
					  "methods" : [{"sl": 119 },],
					  "statements" : [{"sl": 121 },{"sl": 129 },{"sl": 130 },{"sl": 131 },{"sl": 132 },]
					  },
		"test_251" : {
					  "name" : "settingTheSameMatcherIsOk",
					  "pass" : true ,
					  "methods" : [{"sl": 105 },],
					  "statements" : [{"sl": 107 },{"sl": 108 },{"sl": 109 },{"sl": 110 },{"sl": 111 },{"sl": 112 },]
					  },
		"test_248" : {
					  "name" : "errorString",
					  "pass" : true ,
					  "methods" : [{"sl": 78 },{"sl": 82 },{"sl": 86 },],
					  "statements" : [{"sl": 80 },{"sl": 81 },{"sl": 83 },{"sl": 87 },{"sl": 90 },{"sl": 91 },{"sl": 92 },{"sl": 93 },{"sl": 95 },{"sl": 96 },{"sl": 100 },]
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
  [  ] ,
  [  ] ,
  [  ] ,
  [ 233   ] ,
  [  ] ,
  [ 233   ] ,
  [ 233   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 233   ] ,
  [  ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [  ] ,
  [  ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [ 233   ] ,
  [  ] ,
  [  ] ,
  [ 237   ] ,
  [  ] ,
  [ 237   ] ,
  [ 237   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 237   ] ,
  [  ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [  ] ,
  [  ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [  ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [ 237   ] ,
  [  ] ,
  [  ] ,
  [ 248   ] ,
  [  ] ,
  [ 248   ] ,
  [ 248   ] ,
  [ 248   ] ,
  [ 248   ] ,
  [  ] ,
  [  ] ,
  [ 248   ] ,
  [ 248   ] ,
  [  ] ,
  [  ] ,
  [ 248   ] ,
  [ 248   ] ,
  [ 248   ] ,
  [ 248   ] ,
  [  ] ,
  [ 248   ] ,
  [ 248   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 248   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 251   ] ,
  [  ] ,
  [ 251   ] ,
  [ 251   ] ,
  [ 251   ] ,
  [ 251   ] ,
  [ 251   ] ,
  [ 251   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 260   ] ,
  [  ] ,
  [ 260   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 260   ] ,
  [ 260   ] ,
  [ 260   ] ,
  [ 260   ] ,
  [  ] ,
  [  ] ,
  [ 257   ] ,
  [  ] ,
  [ 257   ] ,
  [  ] ,
  [  ] ,
  [ 257   ] ,
  [  ] ,
  [  ] ,
  [  ] 
];
