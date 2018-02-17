//
//  Process.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Process.h"
#import "Pipeline.h"
#import "Tuple.h"

#define RESPONSE @"Odgovor"
#define REQUEST @"Zahtjev"

@interface Process ()

@property (nonatomic, assign) NSInteger count;
@property (nonatomic, assign) NSInteger processID;
@property (nonatomic, strong) NSDictionary<Tuple *, Pipeline *> *allPipes;
@property (nonatomic, strong) NSArray<Pipeline *> *sendPipes;
@property (nonatomic, strong) NSArray<Pipeline *> *receivePipes;
@property (nonatomic, assign) NSInteger clock;
@property (nonatomic, strong) NSMutableDictionary<NSNumber *, NSNumber *> *response;

@end

@implementation Process

- (instancetype)initWithCount:(NSInteger)count
{
    self = [super init];
    if (self) {
        _count = count;
    }
    return self;
}

- (void)start
{
    [self setupPipes];
    self.processID = -1;
    self.clock = -1;
    for (NSInteger idx = 0; idx < self.count; idx++) {
        if (fork() == 0) {
            self.processID = idx;
            self.clock = idx;
            break;
        }
    }
    if (self.processID != -1) {
        self.response = [NSMutableDictionary new];
        [self closePipesEnd];
        [self extractPipesForCurrentProcess];
        for (int i = 0; i < 2; i++) {
            [self startSections];
        }
        sleep(1);
    } else {
        for (NSInteger idx = 0; idx < self.count;) {
            if (wait(NULL) > 0) {
                idx++;
            }
        }
    }
}

- (void)startSections
{
    Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:self.processID value:self.clock];
    Message *msg = [Message messageWithText:REQUEST timestamp:timestamp];
    for (Pipeline *pipe in self.sendPipes) {
        [pipe sendMessage:msg];
        NSLog(@"Proces %ld salje poruku request procesu %ld", self.processID, pipe.destinationID)
    }

    NSInteger currentClock = self.clock;

    for (Pipeline *pipe in self.receivePipes) {
        Message *message = [pipe receiveMessage];
        self.clock = MAX(self.clock, message.timestamp.value) + 1;
        NSInteger otherClock = message.timestamp.value;
        if (currentClock < otherClock || (currentClock == otherClock && self.processID < message.timestamp.sourceID)) {
            self.response[@(message.timestamp.sourceID)] = @(message.timestamp.value);
        } else {
            Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:self.processID value:otherClock];
            Message *msg = [Message messageWithText:RESPONSE timestamp:timestamp];
            Pipeline *wrPipe = [self pipeForWritingTo:message.timestamp.sourceID];
            [wrPipe sendMessage:msg];
        }
    }

    for (Pipeline *pipe in self.receivePipes) {
        Message *message = [pipe receiveMessage];
        self.clock = MAX(self.clock, message.timestamp.value) + 1;
    }

    [self criticalSection];

    [self.response enumerateKeysAndObjectsUsingBlock:^(NSNumber *key, NSNumber * obj, BOOL * _Nonnull stop) {
        if ([key integerValue] >= 0) {
            Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:self.processID value:[obj integerValue]];
            Message *msg = [Message messageWithText:RESPONSE timestamp:timestamp];
            Pipeline *wrPipe = [self pipeForWritingTo:[key integerValue]];
            [wrPipe sendMessage:msg];
            NSLog(@"Proces %ld %ld salje poruku response procesu %ld", self.processID, wrPipe.sourceID, wrPipe.destinationID)
            self.response[key] = @(-1);
        }
    }];
}

- (void)criticalSection
{
    for (NSInteger counter = 1; counter <= 5; counter++) {
        NSLog(@"Proces: %ld, poziv: %ld", self.processID, counter);
        self.clock += 1;
        sleep(1);
    }
}

- (void)setupPipes
{
    NSMutableDictionary<Tuple *, Pipeline *> *pipes = [NSMutableDictionary new];
    for (NSInteger idx = 0; idx < self.count; idx++) {
        for (NSInteger idy = 0; idy < self.count; idy++) {
            if (idx == idy) {
                continue;
            }
            Pipeline *pipe = [Pipeline pipeWithSourceID:idx destinationID:idy];
            Tuple *key = [Tuple tupleWithFirst:@(idx) second:@(idy)];
            pipes[key] = pipe;
        }
    }
    self.allPipes = [NSDictionary dictionaryWithDictionary:pipes];
}

- (void)closePipesEnd
{
    [self.allPipes enumerateKeysAndObjectsUsingBlock:^(Tuple *key, Pipeline *obj, BOOL *stop) {
        if (obj.sourceID == self.processID) {
            [obj closePipeFor:PipeOperationRead];
        } else if (obj.destinationID == self.processID) {
            [obj closePipeFor:PipeOperationWrite];
        }
    }];
}

- (Pipeline *)pipeForWritingTo:(NSInteger)toID
{
    for (Pipeline *pipe in self.sendPipes) {
        if (pipe.destinationID == toID) {
            return pipe;
        }
    }
    return nil;
}

- (void)extractPipesForCurrentProcess
{
    NSMutableArray<Pipeline *> *sendPipes = [NSMutableArray new];
    NSMutableArray<Pipeline *> *receivePipes = [NSMutableArray new];

    [self.allPipes enumerateKeysAndObjectsUsingBlock:^(Tuple *key, Pipeline *obj, BOOL *stop) {
        if (obj.sourceID == self.processID) {
            [sendPipes addObject:obj];
        } else if (obj.destinationID == self.processID) {
            [receivePipes addObject:obj];
        }
    }];
    self.sendPipes = [NSArray arrayWithArray:sendPipes];
    self.receivePipes = [NSArray arrayWithArray:receivePipes];
}

@end
