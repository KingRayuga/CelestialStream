# CelestialStream - A Java BitTorrent Client

CelestialStream is a cutting-edge BitTorrent client crafted in Java, leveraging the power of Maven for seamless dependency management. This project offers a comprehensive set of features for an unparalleled BitTorrent experience, from BEncode decoding to a custom peer handshake.

## Features

### BEncode Decoder and Encoder
Effortlessly handle the BEncode encoding and decoding for efficient parsing of BitTorrent metadata.

### Multithreaded Multi-file .torrent Parser
Parse multi-file .torrent files with ease using a multithreaded approach, optimizing the parsing process for improved performance.

### Info and Piece Hash SHA-1 Calculation
Calculate and verify SHA-1 hashes for both info and piece data, ensuring data integrity throughout the download process.

### Tracker Communication Utility
Efficiently send requests to trackers and parse their responses, enabling seamless communication with the BitTorrent network.

### Custom BitTorrent Peer Handshake
Implement a custom peer handshake for establishing connections, enhancing compatibility and control over the peer interactions.

### Piece Downloader (One Peer at a Time)
Efficiently download pieces from a single peer at a time, ensuring robust and reliable download processes.

### Executor Service for Load Balancing
Utilize an Executor service that dynamically adjusts the number of threads based on the number of available cores, ensuring optimal load balancing and download efficiency.

## Usage

![](https://i.imgur.com/YKeg4SD.png)


### Decode with Several BEncoded Strings

```bash
java -jar CelestialStream.jar decode bencoded_string1 bencoded_string2 ...
```

### Parse with Several .torrent Files

```bash
java -jar CelestialStream.jar parser file1.torrent file2.torrent ...
```

### Download with Several .torrent Files

```bash
java -jar CelestialStream.jar download file1.torrent file2.torrent ...
```

