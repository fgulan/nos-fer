//
//  Pipeline.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Pipeline.h"

#define MAXREAD 128

@interface Pipeline ()

@property (nonatomic, assign) NSInteger sourceID;
@property (nonatomic, assign) NSInteger destinationID;
@property (nonatomic, assign) int readDescriptor;
@property (nonatomic, assign) int writeDescriptor;

@end

@implementation Pipeline

- (instancetype)initWithSourceID:(NSInteger)sourceID destinationID:(NSInteger)destinationID
{
    self = [super init];
    if (!self) {
        return nil;
    }
    _sourceID = sourceID;
    _destinationID = destinationID;
    if (![self setupPipe]) {
        return nil;
    }
    return self;
}

- (BOOL)setupPipe
{
    int fileDescriptors[2];
    if (pipe(fileDescriptors) == -1) {
        return false;
    }
    self.readDescriptor = fileDescriptors[PipeOperationRead];
    self.writeDescriptor = fileDescriptors[PipeOperationWrite];
    return true;
}

+ (instancetype)pipeWithSourceID:(NSInteger)sourceID destinationID:(NSInteger)destinationID
{
    Pipeline *pipe = [[Pipeline alloc] initWithSourceID:sourceID destinationID:destinationID];
    return pipe;
}

#pragma mark - Public methods

- (void)closePipeFor:(PipeOperation)type
{
    close(type == PipeOperationRead ? self.readDescriptor : self.writeDescriptor);
}

- (BOOL)sendMessage:(Message *)message
{
    NSString *string = message.serialized;
    unsigned long size = strlen([string UTF8String]);
    char command[size];
    memcpy(command, [string UTF8String], size);
    write(self.writeDescriptor, command, strlen(command) + 1);
    return YES;
}

- (Message *)receiveMessage
{
    char bytes[MAXREAD] = "";
    if (read(self.readDescriptor, bytes, MAXREAD) == -1) {
        perror("read");
    }
    return [Message messageFromString:[NSString stringWithUTF8String:bytes]];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"Pipeline: %ld -> %ld", self.sourceID, self.destinationID];
}

@end
