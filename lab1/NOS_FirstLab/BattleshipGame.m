//
//  BattleshipGame.m
//  NOS_FirstLab
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "BattleshipGame.h"
#import "Player.h"
#import "MessageQueue.h"

#define USER_INPUT_BUFFER 100
#define HIT_COMMAND @"hit_command"
#define MISS_COMMAND @"miss_command"
#define WIN_COMMAND @"win_command"

@interface BattleshipGame ()

@property (nonatomic, strong) Player *player;
@property (nonatomic, strong) MessageQueue *queue;
@property (nonatomic, assign, readonly) int receiverID;

@end

@implementation BattleshipGame

+ (instancetype)gameWithShips:(UInt32)ships rows:(UInt32)rows cols:(UInt32)cols playerID:(int)playerID
{
    BattleshipGame *game = [[BattleshipGame alloc] init];
    game.player = [Player playerWithID:playerID rows:rows cols:cols ships:ships];
    game.queue = [[MessageQueue alloc] initWithKey:3899];
    if (!game.queue) {
        game = nil;
    }
    return game;
}

- (void)start
{
    if (![self syncPlayer]) {
        return;
    }
    NSLog(@"Players ready. Starting!")
    [self play];
}

- (BOOL)syncPlayer
{
    NSLog(@"%@", self.player.description)
    BOOL sent = [self.queue sendMessage:@"ready"
                         withReceiverID:(self.player.playerID == 1 ? 2 : 1)
                            andSenderID:self.player.playerID];
    NSLog(@"Sync in progress...")
    if (sent) {
        for (;;) {
            Message *message = [self.queue getMessageForReceiverWithID:self.player.playerID];
            if ([message.text isEqualToString:@"ready"]) {
                return YES;
            }
        }
    }
    return NO;
}

- (void)play
{
    for (;;) {
        if (self.player.playerID == 1) {
            printf("Enter target position: ");
            NSString *command = [self readLine];
            [self.queue sendMessage:command
                     withReceiverID:self.receiverID
                        andSenderID:self.player.playerID];
            NSLog(@"Waiting for Player 2...")
            Message *message = [self.queue getMessageForReceiverWithID:self.player.playerID];
            if ([self processMessage:message]) {
                break;
            }
            message = [self.queue getMessageForReceiverWithID:self.player.playerID];
            if ([self processMessage:message]) {
                break;
            }
        } else {
            NSLog(@"Waiting for Player 1...")
            Message *message = [self.queue getMessageForReceiverWithID:self.player.playerID];
            if ([self processMessage:message]) {
                break;
            }
            printf("Enter target position: ");
            NSString *command = [self readLine];
            [self.queue sendMessage:command
                     withReceiverID:self.receiverID
                        andSenderID:self.player.playerID];
            message = [self.queue getMessageForReceiverWithID:self.player.playerID];
            if ([self processMessage:message]) {
                break;
            }
        }
    }
    NSLog(@"Press return key to close app...")
    [self readLine];
}

- (BOOL)processMessage:(Message *)message
{
    if ([message.text isEqualToString:HIT_COMMAND]) {
        NSLog(@"Hit!")
    } else if ([message.text isEqualToString:MISS_COMMAND]) {
        NSLog(@"Miss!")
    } else if ([message.text isEqualToString:WIN_COMMAND]) {
        NSLog(@"Win! Closing...")
        return YES;
    } else {
        return [self processFireCommand:message];
    }
    return NO;
}

- (BOOL)processFireCommand:(Message *)message
{
    BOOL shouldBreak = NO;

    NSArray<NSString *> *positions = [message.text componentsSeparatedByString:@" "];
    NSString *command = nil;
    if (positions.count != 2) {
        command = MISS_COMMAND;
    } else {
        UInt32 row = [positions[0] intValue];
        UInt32 col = [positions[1] intValue];
        BOOL hit = [self.player.ground fireAtRow:row col:col];
        if (self.player.ground.isFinished) {
            command = WIN_COMMAND;
            shouldBreak = YES;
            NSLog(@"Lost! Closing...")
        } else {
            command = hit ? HIT_COMMAND : MISS_COMMAND;
        }
    }
    [self.queue sendMessage:command
             withReceiverID:self.receiverID
                andSenderID:self.player.playerID];
    return shouldBreak;
}

- (int)receiverID
{
    return self.player.playerID == 1 ? 2 : 1;
}

- (NSString *)readLine
{
    char cInput[USER_INPUT_BUFFER];
    fgets(cInput, USER_INPUT_BUFFER, stdin);
    return [[[NSString alloc] initWithCString:cInput encoding:NSUTF8StringEncoding] stringByReplacingOccurrencesOfString:@"\n" withString:@""];
}

@end
