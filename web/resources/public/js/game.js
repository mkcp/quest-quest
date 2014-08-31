var quests = [{
  title: "Welcome to Quest Quest!",
  body: "",
  reward: "Falling Unlocked!"
}, {
  title: "Safety First",
  body: "The ground is fast approaching, you must land safely!",
  reward: "Move Right Unlocked!"
}, {
  title: "Right of Way",
  body: "Get a feel for your surroundings, press d or right arrow to move right as far as your legs will carry you!",
  reward: "Move Left Unlocked!"
}, {
  title: "Left Alone",
  body: "Continue to explore your new surroundings, press press a or left arrow to move left!",
  reward: "Attack Unlocked!"
}, {
  title: "A Trial By Combat",
  body: "Your first enemy blocks the path! You must defeat it to continue. Apply everything you've learned so far to vanquish this beast!",
  reward: "Leveling Up Has Made You Stronger!"
}, {
  title: "Another Trial By Combat",
  body: "A scarier enemiy blocks your path! Vanquish it to proceed!",
  reward: "Leveling Up Has Made You Stronger!"
}, {
  title: "An Elite Leader",
  body: "An extremely cunning and ferocious stands between you and the nearby village! Fight with all your power to overcome it!",
  reward: "Pick-up Item Unlocked!"
}, {
  title: "Johnney Applegatherer",
  body: "Those villagers look very hungry. Gather all of their apples to keep them from starving to death!",
  reward: "Jump Unlocked!"
}, {
  title: "Launch Over It!",
  body: "Tighten the muscles in your legs to form a spring and launch yourself over the rock.",
  reward: "Jump-Attack Unlocked!"
}, {
  title: "The Epic Raid Boss",
  body: "Destroy the final boss to see what lies inside the treasure room!",
  reward: "Open Treasure Unlocked!"
}, {
  title: "End Game",
  body: "Congratulations, you've reached the peak of your power! Surely untold adventures lie before you.",
  reward: "End-Game Content Unlocked!"
}];

// Physics constants
var MAX_SPEED = 400;     // Pixels / second
var JUMP_SPEED = -250;   // Pixels / second (negative y is u p )
var ACCELERATION = 2000; // Pixels / second / second
var DRAG = 2000;         // Pixels / second / second
var GRAVITY = 1200;      // Pixels / second

var gameIsPaused = false;

var GameState = function(game) {};

GameState.prototype.preload = function() {

  // World
  this.game.load.tilemap('world', 'assets/world.json', null, Phaser.Tilemap.TILED_JSON);
  this.game.load.image('tileMap', 'assets/tileMap.png');

  // Entities
  this.game.load.image('player', 'assets/sprites/quester.png');
  this.game.load.image('enemy', 'assets/sprites/first-enemy.png');
};

GameState.prototype.create = function() {

  // Start physics
  this.game.physics.startSystem(Phaser.Physics.ARCADE);

  // Register inputs from keyboard
  this.game.input.keyboard.addKeyCapture([
    Phaser.Keyboard.LEFT,
    Phaser.Keyboard.RIGHT,
    Phaser.Keyboard.UP,
    Phaser.Keyboard.DOWN,
  ]);

  // Create input system
  this.input = new Input();

  // FIXME Implement gradient background
  // var bgBackground = this.game.add.bitmapData(100, 100);
  // bgBackground.beginLinearGradientFill(["#000", "#FFF"], [0, 1], 0, 20, 0, 120);
  // bgBackground.rect(20, 20, 120, 120);
  // bgBackground.fill();
  // this.game.add.sprite(50, 50, bgbackground);

  // Set background
  this.game.stage.backgroundColor = '#7ec0ee';

  // Add world and then tile sheets to world
  this.map = this.game.add.tilemap('world');
  this.map.addTilesetImage('tileMap', 'tileMap');

  this.walls = this.map.createLayer('walls');

  this.walls.resizeWorld();

  // Create player ingame
  var spawn = {
    x: this.game.width / 2,
    y: this.game.height - (this.game.height / 1.33),
  };
  this.player = this.game.add.sprite(spawn.x, spawn.y, 'player');

  // Scale player
  this.player.scale.x = 2;
  this.player.scale.y = 2;

  this.game.physics.enable(this.player);

  // Define player collisions
  this.player.body.collideWorldBounds = true;

  this.player.body.maxVelocity.setTo(MAX_SPEED, MAX_SPEED * 10); // (x, y)
  this.player.body.drag.setTo(DRAG, 0); // (x, y)

  // Set world gravity
  this.game.physics.arcade.gravity.y = GRAVITY;

  // Camera behavior
  this.game.camera.follow(this.player);

  // Create FPS counter
  this.game.time.advancedTiming = true;

  // FIXME Cludgy
  this.map.setCollision([
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
    18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47
  ], true, this.walls);

  var fpsTextStyle = {
    font: '16px Arial',
    fill: '#ffffff'
  };
  this.fpsText = this.game.add.text(20, 20, '', fpsTextStyle);
};

GameState.prototype.update = function() {

  if (!gameIsPaused) {
    this.game.physics.arcade.collide(this.player, this.walls);

    // Handle input
    if (this.input.isLeft()) {
      this.player.body.acceleration.x = -ACCELERATION;
    } else if (this.input.isRight()){
      this.player.body.acceleration.x = ACCELERATION;
    } else {
      this.player.body.acceleration.x = 0;
    }

    if (this.input.isUp() && this.player.body.touching.down) {
      this.player.body.velocity.y = JUMP_SPEED;
    }

    // Update FPS Counter
    if (this.game.time.fps !== 0) {
      this.fpsText.setText(this.game.time.fps + ' FPS');
    }
  }
};

var game = new Phaser.Game(800, 600, Phaser.AUTO, 'quest-quest');
game.state.add('quest-quest', GameState, true);

function Input() {

  this.isLeft = function() {
    var isActive = false;

    isActive = game.input.keyboard.isDown(Phaser.Keyboard.LEFT);
    isActive |= (game.input.activePointer.isDown &&
                 game.input.activePointer.x < game.width / 4);

    return isActive;
  };

  this.isRight = function() {
    var isActive = false;

    isActive = game.input.keyboard.isDown(Phaser.Keyboard.RIGHT);
    isActive |= (game.input.activePointer.isDown &&
                 game.input.activePointer.x > game.width / 2 + game.width / 4);

    return isActive;
  };

  // FIXME Still includes duration
  this.isUp = function (duration) {
    var isActive = false;

    isActive = game.input.keyboard.justPressed(Phaser.Keyboard.UP, duration);
    isActive |= (game.input.activePointer.justPressed(duration + 1000/60) &&
                 game.input.activePointer.x > game.width / 4 &&
                 game.input.activePointer.x < game.width / 2 + game.width / 4);

    return isActive;
  };
}
