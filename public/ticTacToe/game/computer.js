Math.randomInt = function (max) {
  return Math.floor(Math.random() * (max + 1));
};

var computerMaker = function (board, gameTurn) {
  var minimaxDecider = minimaxMaker(board, gameTurn);
  var myTurn = 1;
  var hisTurn = -1;
  var hisSymbol = -1;
  var mySymbol = 1;
  var type = 1;
  var attrs =  {
    currentBoard: board,
    virtualBoard: board.squares.slice(),
    turn: myTurn
  };
  attrs.isMyTurn = function () {
    return myTurn === this.turn;
  };
  attrs.madeMyMove = function () {
    this.turn = hisTurn;
  };
  attrs.heMadeHisMove = function (move) {
    this.virtualBoard[move] = hisSymbol;
    this.turn = myTurn;
  };
  attrs.setToSmartComputer = function () {
    type = 1;
  };
  attrs.isSmart = function () {
    return type === 1;
  }
  attrs.setToDumbComputer = function () {
    type = 0;
  };
  attrs.makeMove = function () {
    if (type === 1)
      return this.makeSmartMove();
    return this.makeRandomMove();
  };
  attrs.makeRandomMove = function () {
    if (this.currentBoard.catsGame())
      return -1;
    var randomNum = Math.randomInt(8);
    while (this.virtualBoard[randomNum] !== 0)
      randomNum = Math.randomInt(8);
    this.virtualBoard[randomNum] = mySymbol;
    this.madeMyMove();
    return randomNum;
  };
  attrs.makeSmartMove = function () {
    if (this.currentBoard.catsGame())
      return -1;
    minimaxDecider.setVirtualBoard(this.virtualBoard);
    minimaxDecider.setTurn(myTurn);
    var move = minimaxDecider.findBestMove();
    this.virtualBoard[move] = mySymbol;
    this.madeMyMove();
    return move;
  };
  return attrs;
};

var minimaxMaker = function (board, turn) {
  var virtualBoard = {
    squares: board.squares.slice()
  };
  var currentPlayer = turn;
  return {
    setVirtualBoard : function (newSquares) {
      virtualBoard.squares = newSquares.slice();
    },
    setTurn : function (player) {
      currentPlayer = player;
    },
    getAllMoves : function () {
      var moves = [];
      var move;
      for (move = 0; move < 9; move += 1) {
        if (virtualBoard.squares[move] === 0)
          moves.push(move);
      }
      return moves;
    },
    scoreMove : function (move) {
      var steppingBack = function () {
        virtualBoard.squares[move] = 0;
      };
      var nextPlayer = currentPlayer * -1;
      var currentScore;
      var finalScore = -2;
      if (virtualBoard.squares[move] !== 0 || move < 0 || move > 8)
        return "Invalid Move";
      virtualBoard.squares[move] = currentPlayer;
      currentScore = board.checkWin.apply(virtualBoard);
      if (currentScore === currentPlayer){
        steppingBack();
        return currentScore;
      }
      if (board.catsGame.apply(virtualBoard)){
        steppingBack();
        return 0;
      }
      var moves = this.getAllMoves();
      var newMove;
      for(newMove in moves) {
        newMove = moves[newMove];
        currentPlayer = nextPlayer;
        currentScore = this.scoreMove(newMove);
        currentPlayer = currentPlayer * -1;
        
        if(currentScore * nextPlayer > finalScore)
          finalScore = currentScore * nextPlayer;
      }
      steppingBack();
      return finalScore * nextPlayer;
    },

    findBestMove: function (){
      var moves = this.getAllMoves();
      var move;
      var bestMove;
      var bestScore = -2;
      for (move in moves) {
        if (moves.length === 9)
          return 4; //the center!
        move = moves[move];
        var score = this.scoreMove(move);
        if( score > bestScore){
          bestMove = move;
          bestScore = score;
        }
        if (score === 1)
          return move;
      }
      return bestMove;
    }
  };
    
};
