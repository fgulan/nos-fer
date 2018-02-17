//
//  main.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 19/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Pipeline.h"
#import "Process.h"

#define COUNT 3
#define RESPONSE @"Odgovor"
#define REQUEST @"Zahtjev"

NSDictionary<NSString *, Pipeline *> *pipelines;
NSInteger processID;
NSInteger logicClock = 1;
NSArray<Pipeline *>* readPipelines;
NSArray<Pipeline *>* writePipelines;

Pipeline* getPipeline(NSInteger sourceID, NSInteger destinationID)
{
    NSString *key = [NSString stringWithFormat:@"%ld -> %ld", sourceID, destinationID];
    return pipelines[key];
}

NSDictionary<NSString *, Pipeline *>* setupPipelines(NSInteger count)
{
    NSMutableDictionary<NSString *, Pipeline *> *mutableDict = [NSMutableDictionary new];
    for (NSInteger idx = 0; idx < count; idx++) {
        for (NSInteger idy = 0; idy < count; idy++) {
            if (idx == idy) {
                continue;
            }
            NSString *key = [NSString stringWithFormat:@"%ld -> %ld", idx, idy];
            Pipeline *pipe = [Pipeline pipeWithSourceID:idx destinationID:idy];
            mutableDict[key] = pipe;
        }
    }
    return [NSDictionary dictionaryWithDictionary:mutableDict];
}

void extractProcessPipelines()
{
    NSMutableArray<Pipeline *> *readPipes = [NSMutableArray new];
    NSMutableArray<Pipeline *> *writePipes = [NSMutableArray new];
    for (NSInteger idx = 0; idx < COUNT; idx++) {
        if (idx != processID) {
            Pipeline *writePipe = getPipeline(processID, idx);
            Pipeline *readPipe = getPipeline(idx, processID);
            [writePipe closePipeFor:PipeOperationRead];
            [readPipe closePipeFor:PipeOperationWrite];
            [readPipes addObject:readPipe];
            [writePipes addObject:writePipe];
        }
    }
    readPipelines = [NSArray arrayWithArray:readPipes];
    writePipelines = [NSArray arrayWithArray:writePipes];
}

void sendRequests()
{
    Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:processID value:logicClock];
    Message *msg = [Message messageWithText:REQUEST timestamp:timestamp];
    for (Pipeline *pipe in writePipelines) {
        [pipe sendMessage:msg];
        NSLog(@"Proces P%ld salje procesu P%ld poruku %@", processID, pipe.destinationID, msg)
    }
}

void criticalSection()
{
    for (NSInteger counter = 1; counter <= 5; counter++) {
        NSLog(@"Proces: %ld, poziv: %ld", processID, counter);
        logicClock += 1;
        sleep(1);
    }
}

void work()
{
    sendRequests();
    NSInteger currentClock = logicClock;

    NSMutableDictionary<NSNumber *, NSNumber *> *response = [NSMutableDictionary new];
    for (Pipeline *pipe in readPipelines) {
        Message *message = [pipe receiveMessage];
        NSLog(@"Proces P%ld primio poruku %@", processID, message)
        NSInteger otherClock = message.timestamp.value;
        logicClock = MAX(logicClock, otherClock) + 1;


        if ((currentClock < otherClock) || (currentClock == otherClock && processID < message.timestamp.sourceID)) {
            response[@(message.timestamp.sourceID)] = @(message.timestamp.value);
        } else {
            Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:processID value:otherClock];
            Message *msg = [Message messageWithText:RESPONSE timestamp:timestamp];
            Pipeline *wrPipe = getPipeline(processID, message.timestamp.sourceID);
            [wrPipe sendMessage:msg];
            NSLog(@"Proces P%ld salje procesu P%ld poruku %@", processID, wrPipe.destinationID, msg)
        }
    }

    for (Pipeline *pipe in readPipelines) {
        Message *message = [pipe receiveMessage];
        NSLog(@"Proces P%ld primio poruku %@", processID, message)
        logicClock = MAX(logicClock, message.timestamp.value) + 1;
    }

    criticalSection();
    [response enumerateKeysAndObjectsUsingBlock:^(NSNumber *key, NSNumber * obj, BOOL * _Nonnull stop) {
        if ([key integerValue] >= 0) {
            Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:processID value:[obj integerValue]];
            Message *msg = [Message messageWithText:RESPONSE timestamp:timestamp];
            Pipeline *wrPipe = getPipeline(processID, [key integerValue]);
            [wrPipe sendMessage:msg];
            NSLog(@"Proces P%ld salje procesu P%ld poruku %@", processID, wrPipe.destinationID, msg)
            response[key] = @(-1);
        }
    }];
}

void process()
{
    extractProcessPipelines();
    for (NSInteger count = 0; count < 2; count++) {
        work();
    }
}

int main(int argc, const char * argv[])
{
    @autoreleasepool
    {
        pipelines = setupPipelines(COUNT);
        for (NSInteger idx = 0; idx < COUNT; idx++) {
            NSInteger pid = fork();
            if (pid == - 1) {
                NSLog(@"Error");
                exit(0);
            } else if (pid == 0) {
                processID = idx;
                logicClock = idx;
                process();
                exit(0);
            }
        }
        for (NSInteger idx = 0; idx < COUNT;) {
            if (wait(NULL) > 0) {
                idx++;
            }
        }
        exit(0);
    }
    return 0;
}
