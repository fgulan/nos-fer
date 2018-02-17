//
//  Pipeline.h
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Message.h"

typedef NS_ENUM(int, PipeOperation){
    PipeOperationRead = 0,
    PipeOperationWrite = 1
};

@interface Pipeline : NSObject

@property (nonatomic, assign, readonly) NSInteger sourceID;
@property (nonatomic, assign, readonly) NSInteger destinationID;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithSourceID:(NSInteger)sourceID destinationID:(NSInteger)destinationID NS_DESIGNATED_INITIALIZER;
+ (instancetype)pipeWithSourceID:(NSInteger)sourceID destinationID:(NSInteger)destinationID;

- (void)closePipeFor:(PipeOperation)type;
- (BOOL)sendMessage:(Message *)message;
- (Message *)receiveMessage;

@end
