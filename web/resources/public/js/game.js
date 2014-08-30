var game = new Phaser.Game(800, 600, Phaser.AUTO, 'Quest Quest', {preload: preload, create: create, update: update, render: render});

var MAX_SPEED = 500;     // Pixels / second
var JUMP_SPEED = -250;   // Pixels / second (negative y is u p )
var ACCELERATION = 1200; // Pixels / second / second
var DRAG = 2400;         // Pixels / second / second
var GRAVITY = 2400;      // Pixels / second

function preload() {
  game.load.image('player', 'assets/sprites/quester.png');
  game.load.image('enemy', 'assets/sprites/first-enemy.png');
  game.load.tilemap('world', 'assets/world.tmx', null, Phaser.Tilemap.TILED_XML);
}

// FIXME fill in data from previous questquest.js

function create() {
  game.stage.backgroundColor = '#007236';
}

function update() {
}

function render() {
}
