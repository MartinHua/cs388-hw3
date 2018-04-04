-------------------------------------------------
README : cs388-hw3
-------------------------------------------------

---------------------------
AUTHOR
---------------------------
 Xinrui Hua, UT EID: xh3426
 
-------------------------------
EXECUTING CODE FOR TESTING
-------------------------------
1. After you unzip the project folder, navigate to project path in command line: cd /"path to project folder"/xinrui_hua_xh3426/cs388-hw3/active-learning
2. There are five argments:
  1. training mode: random, length, raw and margin
  2. train set path
  3. unlabeled set path
  4. test set path
  5. total train iterations: 20
  6. iterations for each model: 50
  
  Run in command line: 

  java edu/stanford/nlp/parser/nndep/DependencyParserAPIUsage random ./train.conllx ./unlabeled.conllx ./test.conllx 20 50
