//
//  Message.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Message.h"

@interface Message ()

@property (nonatomic, strong) Timestamp *timestamp;
@property (nonatomic, copy) NSString *text;

@end

@implementation Message

- (instancetype)initWithText:(NSString *)text timestamp:(Timestamp *)timestamp
{
    if (self = [super init]) {
        _timestamp = timestamp;
        _text = text;
    }
    return self;
}

+ (instancetype)messageWithText:(NSString *)text timestamp:(Timestamp *)timestamp
{
    return [[Message alloc] initWithText:text timestamp:timestamp];
}

- (NSString *)serialized
{
    return [NSString stringWithFormat:@"%ld %ld %@", (long)self.timestamp.sourceID, (long)self.timestamp.value, self.text];
}

+ (instancetype)messageFromString:(NSString *)string
{
    NSArray<NSString *> *components = [string componentsSeparatedByString:@" "];
    if (components.count != 3) {
        return nil;
    }
    Timestamp *timestamp = [[Timestamp alloc] initWithSourceID:[components[0] integerValue]
                                                     value:[components[1] integerValue]];
    return [Message messageWithText:components[2] timestamp:timestamp];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@(%ld, %ld)", self.text, self.timestamp.sourceID, self.timestamp.value];
}

@end
