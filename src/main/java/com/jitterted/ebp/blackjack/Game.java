package com.jitterted.ebp.blackjack;

import org.fusesource.jansi.Ansi;

import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class Game {

    private final Deck deck;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    public static void main(String[] args) {
        displayWelcome();
        playGame();
        resetScreen();
    }

    private static void playGame() {
        Game game = new Game();
        game.initialDeal();
        game.play();
    }

    private static void resetScreen() {
        System.out.println(ansi().reset());
    }

    private static void displayWelcome() {
        System.out.println(ansi()
                                   .bgBright(Ansi.Color.WHITE)
                                   .eraseScreen()
                                   .cursor(1, 1)
                                   .fgGreen().a("Welcome to")
                                   .fgRed().a(" Jitterted's")
                                   .fgBlack().a(" BlackJack"));
    }

    public Game() {
        deck = new Deck();
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
    }

    private void dealRoundOfCards() {
        // players first because that's the Rule of Blackjack's initial dealing of cards
        playerHand.drawCardFrom(deck);
        dealerHand.drawCardFrom(deck);
    }

    public void play() {
        boolean playerBusted = playerTurn();

        dealerTurn(playerBusted);

        displayFinalGameState();

        determineOutcome(playerBusted);
    }

    private void determineOutcome(boolean playerBusted) {
        if (playerBusted) {
            System.out.println("You Busted, so you lose.  💸");
        } else if (dealerHand.isBusted()) {
            System.out.println("Dealer went BUST, Player wins! Yay for you!! 💵");
        } else if (playerHand.beats(dealerHand)) {
            System.out.println("You beat the Dealer! 💵");
        } else if (playerHand.pushes(dealerHand)) {
            System.out.println("Push: You tie with the Dealer. 💸");
        } else {
            System.out.println("You lost to the Dealer. 💸");
        }
    }

    private void dealerTurn(boolean playerBusted) {
        // Dealer makes its choice automatically based on a simple heuristic (<=16, hit, 17>=stand)
        if (!playerBusted) {
            while (dealerHand.shouldDealerHit()) {
                dealerHand.drawCardFrom(deck);
            }
        }
    }

    private boolean playerTurn() {
        // get Player's decision: hit until they stand, then they're done (or they go bust)
        boolean playerBusted = false;
        while (!playerBusted) {
            displayGameState();
            String playerChoice = inputFromPlayer().toLowerCase();
            if (playerStands(playerChoice)) {
                break;
            }
            if (playerHits(playerChoice)) {
                playerHand.drawCardFrom(deck);
                if (playerHand.isBusted()) {
                    playerBusted = true;
                }
            } else {
                System.out.println("You need to [H]it or [S]tand");
            }
        }
        return playerBusted;
    }

    private boolean playerHits(String playerChoice) {
        return playerChoice.startsWith("h");
    }

    private boolean playerStands(String playerChoice) {
        return playerChoice.startsWith("s");
    }

    private String inputFromPlayer() {
        System.out.println("[H]it or [S]tand?");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void displayGameState() {
        eraseScreen();

        displayDealerHandDuringGame();

        displayPlayer();
    }

    private void displayPlayer() {
        System.out.println();
        System.out.println("Player has: ");
        playerHand.display();
        System.out.println(" (" + playerHand.value() + ")");
    }

    private void displayDealerHandDuringGame() {
        System.out.println("Dealer has: ");
        System.out.println(dealerHand.firstCard().display()); // first card is Face Up

        // second card is the hole card, which is hidden
        displayBackOfCard();
    }

    private void eraseScreen() {
        System.out.print(ansi().eraseScreen().cursor(1, 1));
    }

    private void displayBackOfCard() {
        System.out.print(
                ansi()
                        .cursorUp(7)
                        .cursorRight(12)
                        .a("┌─────────┐").cursorDown(1).cursorLeft(11)
                        .a("│░░░░░░░░░│").cursorDown(1).cursorLeft(11)
                        .a("│░ J I T ░│").cursorDown(1).cursorLeft(11)
                        .a("│░ T E R ░│").cursorDown(1).cursorLeft(11)
                        .a("│░ T E D ░│").cursorDown(1).cursorLeft(11)
                        .a("│░░░░░░░░░│").cursorDown(1).cursorLeft(11)
                        .a("└─────────┘"));
    }

    private void displayFinalGameState() {
        eraseScreen();

        displayFinalDealerState();

        displayPlayer();
    }

    private void displayFinalDealerState() {
        System.out.println("Dealer has: ");
        dealerHand.display();
        System.out.println(" (" + dealerHand.value() + ")");
    }
}
