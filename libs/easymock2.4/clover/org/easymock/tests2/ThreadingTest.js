var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = { "classes" : [
    {"id" : 5557, "sl" : 23, "el" : 90, "name" : "ThreadingTest",
    "methods" : [
              {"sl" : 27, "el" : 54, "sc" : 5},  {"sl" : 38, "el" : 40, "sc" : 13},  {"sl" : 56, "el" : 89, "sc" : 5},  {"sl" : 65, "el" : 67, "sc" : 13}  ]}
    
 ]
};

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {
		"test_147" : {
					  "name" : "testThreadSafe",
					  "pass" : true ,
					  "methods" : [{"sl": 27 },{"sl": 38 },],
					  "statements" : [{"sl": 30 },{"sl": 31 },{"sl": 33 },{"sl": 35 },{"sl": 37 },{"sl": 39 },{"sl": 43 },{"sl": 45 },{"sl": 47 },{"sl": 49 },{"sl": 50 },{"sl": 53 },]
					  },
		"test_132" : {
					  "name" : "testThreadNotSafe",
					  "pass" : true ,
					  "methods" : [{"sl": 56 },{"sl": 65 },],
					  "statements" : [{"sl": 59 },{"sl": 60 },{"sl": 62 },{"sl": 64 },{"sl": 66 },{"sl": 70 },{"sl": 72 },{"sl": 74 },{"sl": 76 },{"sl": 78 },{"sl": 79 },{"sl": 80 },{"sl": 83 },{"sl": 84 },{"sl": 88 },]
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
  [ 147   ] ,
  [  ] ,
  [  ] ,
  [ 147   ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [ 147   ] ,
  [ 147   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [ 147   ] ,
  [ 147   ] ,
  [  ] ,
  [  ] ,
  [ 147   ] ,
  [  ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [  ] ,
  [ 132   ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [ 132   ] ,
  [ 132   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [ 132   ] ,
  [ 132   ] ,
  [ 132   ] ,
  [  ] ,
  [  ] ,
  [ 132   ] ,
  [ 132   ] ,
  [  ] ,
  [  ] ,
  [  ] ,
  [ 132   ] ,
  [  ] ,
  [  ] ,
  [  ] 
];
