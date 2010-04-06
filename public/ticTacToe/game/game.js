document.addAnotherJavaScriptFile = function (filePath) {
  var head = document.getElementsByTagName("head")[0];
  var newScript = document.createElement('script');
  newScript.setAttribute('type', 'text/javascript');
  newScript.setAttribute('src', filePath);
  head.appendChild(newScript);
};

document.addAnotherJavaScriptFile("game/computer.js");
var GAME = {};
var COMPUTER;
var makeGame = function (players) {
  var setNameFor = function (player,name) {
    inputedName = document.getElementById(player);
    GAME[player] = (inputedName === null || inputedName.value === "") ? name : inputedName.value;
  };
  setNameFor("player1", "The Loathed One");
  var player2Name = players === 2 ? "Player 2" : "John Conway";
  setNameFor("player2", player2Name);
  GAME.players = players;
  GAME.boxes = [0,0,0,0,0,0,0,0,0];
  GAME.board = makeBoard();
  GAME.turn = 0;
  if (players === 1)
    createTheComputer();
  makeBody();
};

var createTheComputer = function () {
  COMPUTER = computerMaker(GAME.board, 0);
};

var makeBody = function () {
  var text= "<pre><h1>Tic-Tac-Toe!</h1>";
  if (GAME.players === 1){
    text += "<h3>YOU ARE MINE!</h3>";
    text += "<input type='button' id='computerGo' value='Go First if you are so confident...' onclick='goComputer()' />";
    text += "<input type='button' id='computerAI' value='Dumb Computer' onclick='setComputerAI()' /> <br/>";
  }
  var makeBoxes = function () {
    var i;
    for(i = 0; i < 9; i += 1){
      text += "<img border='1' src='images/whitebox.jpg' id='"+i+"' width='50' height='50'" +
      " onclick='makeMove("+i+")'/>";
      if (i === 2 || i===5)
        text += "<br/>";
    }
  }();
  var addPlayerName = function () {
    var name = GAME.turn === 0 ? GAME.player1 : GAME.player2;
    text += "<br/><input type='text' id='playerName' value='" + name + "' />";
  }();
  text+= "</pre>";
  document.body.innerHTML = text;
}

var setComputerAI = function () {
  element = document.getElementById("computerAI");
  element.type = "hidden";
  COMPUTER.setToDumbComputer();
};

var goComputer = function () {
  document.getElementById("computerGo").value = "Go Again.";
  var compMove = COMPUTER.makeMove();
    if (compMove === -1)
      return "CATS";
    markBox(compMove);
    if (isGameOver())
      return actForGameOver();
    document.getElementById(compMove).src = GAME.boxes[compMove];
    return true;
};

var makeMove = function (move) {
  markBox(move);
  if (isGameOver())
    return actForGameOver();
  document.getElementById(move).src = GAME.boxes[move];
  if (GAME.players === 1) {
    COMPUTER.heMadeHisMove(move);
    return goComputer();
  }
};

var markBox = function (box) {
  var nameBox = document.getElementById("playerName");
  var markX = function () {
    GAME.boxes[box] = "images/x.jpg";
    GAME.board.squares[box] = 1;
    GAME.turn = 1;
    GAME.player1 = nameBox.value;
    nameBox.value = GAME.player2;
  };

  var markO = function () {
    GAME.boxes[box] = "images/o.jpg";
    GAME.board.squares[box] = -1;
    GAME.turn = 0;
    GAME.player2 = nameBox.value;
    nameBox.value = GAME.player1;
  };
  if (GAME.boxes[box] !==0)
    return "Box Already Used";
  if (GAME.turn === 0)
    markX();
  else
    markO();
};

var makeBoard = function () {
  var board = {
    squares: [0,0,0,0,0,0,0,0,0]
  };
  board.checkWin = function () {
    var that = this;
    var checkrow = function (row)
    {
      row = row * 3;
      if ((that.squares[row] !== 0) && (that.squares[row] === that.squares[row + 1] &&
              that.squares[row + 1] === that.squares[row +2]))
        return that.squares[row];
      return 0;
    };

    var checkcollumn = function (collumn)
    {
      if ((that.squares[collumn] !== 0) && 
        (that.squares[collumn] === that.squares[collumn + 3] &&
        that.squares[collumn + 3] === that.squares[collumn + 6]))
        return that.squares[collumn];
      return 0;
    };

    var checkdiagonals = function ()
    {
      if ((that.squares[4] !== 0) && 
      ((that.squares[0] === that.squares[4] && that.squares[4] === that.squares[8]) || 
      (that.squares[2] === that.squares[4] && that.squares[4] === that.squares[6])))
        return that.squares[4];
      return 0;
    };

    var i;
    for (i = 0; i < 3; i += 1){
      if (checkrow(i) !== 0)
        return checkrow(i);
      if (checkcollumn(i) !== 0)
        return checkcollumn(i);
    }
    return checkdiagonals();
  };

  board.catsGame = function () {
    var i;
    for(i = 0; i < 9; i += 1){
      if (this.squares[i] === 0)
        return false;
    }
    return true;
  };
  return board;
};

var isGameOver = function () {
  return (GAME.board.checkWin() !== 0 || GAME.board.catsGame());
}

var actForGameOver = function () {

  if(GAME.board.checkWin() !== 0){
    return setGameWinner();
  }
  if (GAME.board.catsGame()){
    return setCatsGame();
  }
  return false;
}

var setGameWinner = function () {
  var winner = GAME.turn === 0 ? GAME.player2 : GAME.player1;
  var text = "<pre><h1>" + winner + " Has Won!!</h1>";
  text += makeEndGameBoxes();
  text += "</pre>";
  document.body.innerHTML = text;
  return "WIN"
};
var setCatsGame = function () {
  var text = "<pre><h1> Cats Game... BORING!</h1>";
  text += makeEndGameBoxes();
  text += "</pre>";
  document.body.innerHTML = text;
  return "CATS";
};

var makeEndGameBoxes = function () {
  var text= "";
  var i;
  for (i=0; i< 9; i+= 1){
    var box = GAME.boxes[i] === 0 ? "images/whitebox.jpg" : GAME.boxes[i];
    text += "<img border='1' src='"+box+"' width='50' height='50' />";
    if (i === 2 || i === 5)
      text += "<br/>";
  }
  return text;
}





