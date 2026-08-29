package com.example.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.MangaItem
import com.example.data.model.MangaType
import com.example.ui.components.NexusMangaImage
import com.example.ui.theme.BadgeNew
import com.example.ui.theme.BadgeSuccess
import com.example.ui.theme.NexusCrimson
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusGold
import com.example.ui.theme.NexusPurple
import com.example.ui.theme.NexusPurpleDark
import com.example.ui.theme.NexusPurpleLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.DetailsUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUiState,
    onNavigateHome: () -> Unit,
    onChapterClick: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onBatchIndexChange: (Int) -> Unit,
    onNextBatch: () -> Unit,
    onPreviousBatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val manga = uiState.manga

    if (manga == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("جاري تحميل تفاصيل العمل...", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("details_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // Top Navigation Bar (With Return to Home Button)
        item {
            DetailsTopAppBar(
                onNavigateHome = onNavigateHome,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }

        // Cover & Title Header Card
        item {
            MangaHeaderCard(manga = manga)
        }

        // Categories / Genres (التصنيفات)
        item {
            CategoriesSection(genres = manga.genres)
        }

        // Short Synopsis / Story (القصة القصيرة)
        item {
            SynopsisSection(synopsis = manga.synopsis)
        }

        // Staff / Production Team (العاملين عليها)
        item {
            StaffCreditsSection(manga = manga)
        }

        // Action Buttons: "متابعة القراءة" & "الفصل الأول" (Above the chapters list)
        item {
            ActionButtonsSection(
                lastReadChapter = uiState.lastReadChapterNumber,
                onContinueReading = { onChapterClick(uiState.lastReadChapterNumber) },
                onFirstChapter = { onChapterClick(1) }
            )
        }

        // Section Title: Chapters List Header with Batch Range Info
        item {
            ChaptersHeaderSection(
                totalChapters = manga.totalChaptersCount,
                rangeText = uiState.currentBatchRangeText,
                currentBatch = uiState.currentBatchIndex + 1,
                totalBatches = uiState.totalBatches
            )
        }

        // 30-Chapter Batch List (كل مرة 30 فصل)
        val chapters = uiState.currentBatchChapters
        items(chapters) { chapter ->
            ChapterListItem(
                chapter = chapter,
                isRead = chapter.number <= uiState.lastReadChapterNumber,
                onClick = { onChapterClick(chapter.number) }
            )
        }

        // Pagination Switcher Controls at the bottom (زر التبديل بين الـ 30 فصل)
        item {
            BatchPaginationControl(
                currentBatchIndex = uiState.currentBatchIndex,
                totalBatches = uiState.totalBatches,
                batchSize = uiState.batchSize,
                totalChapters = manga.totalChaptersCount,
                onSelectBatch = onBatchIndexChange,
                onPreviousBatch = onPreviousBatch,
                onNextBatch = onNextBatch,
                onNavigateHome = onNavigateHome
            )
        }
    }
}

@Composable
fun DetailsTopAppBar(
    onNavigateHome: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Return to Home Button (زر العودة للصفحة الرئيسية)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, SurfaceElevated),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onNavigateHome() }
                .testTag("details_back_home_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "العودة للرئيسية",
                    tint = NexusPurpleLight,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "الرئيسية",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        }

        Text(
            text = "تفاصيل العمل",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        // Favorite Toggle Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceCard)
                .border(1.dp, SurfaceElevated, CircleShape)
                .testTag("details_fav_button")
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "المفضلة",
                tint = if (isFavorite) NexusCrimson else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun MangaHeaderCard(manga: MangaItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Work Cover Image
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(8.dp)
            ) {
                NexusMangaImage(
                    imageUrl = manga.coverUrl,
                    fallbackRes = manga.coverRes,
                    contentDescription = manga.titleAr,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = if (manga.type == MangaType.MANHWA) NexusPurple else NexusCrimson,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = manga.type.labelAr,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Manga Metadata
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = manga.titleAr,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                )

                Text(
                    text = manga.titleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = NexusGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${manga.rating}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NexusGold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = NexusCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = manga.views,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceVariantDark,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "الحالة: ${manga.status} • ${manga.totalChaptersCount} فصل",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NexusPurpleLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Categories / Genres Section (التصنيفات)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesSection(genres: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "التصنيفات",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            genres.forEach { genre ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceVariantDark,
                    border = BorderStroke(1.dp, SurfaceElevated)
                ) {
                    Text(
                        text = genre,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/**
 * Short Story / Synopsis Section (القصة القصيرة)
 */
@Composable
fun SynopsisSection(synopsis: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(3.dp, 14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(NexusPurple)
                )
                Text(
                    text = "القصة القصيرة",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Text(
                text = synopsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    fontSize = 13.sp
                ),
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (expanded) "عرض أقل ▲" else "قراءة المزيد ▼",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NexusPurpleLight,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}

/**
 * Staff & Team Section (العاملين عليها)
 */
@Composable
fun StaffCreditsSection(manga: MangaItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = NexusCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "العاملين على العمل والترجمة",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StaffInfoRow(icon = Icons.Default.Edit, label = "المؤلف", value = manga.author)
                StaffInfoRow(icon = Icons.Default.Person, label = "الرسام", value = manga.artist)
                StaffInfoRow(icon = Icons.Default.Translate, label = "فريق الترجمة", value = manga.scanlationTeam)
                StaffInfoRow(icon = Icons.Default.Translate, label = "المترجم", value = manga.translator)
                StaffInfoRow(icon = Icons.Default.Edit, label = "التبييض والتحرير", value = "${manga.cleaner} / ${manga.typesetter}")
            }
        }
    }
}

@Composable
fun StaffInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 12.sp
            )
        )
    }
}

/**
 * Action Buttons placed directly above the chapter list:
 * - "متابعة القراءة" (Continue Reading)
 * - "الفصل الأول" (First Chapter)
 */
@Composable
fun ActionButtonsSection(
    lastReadChapter: Int,
    onContinueReading: () -> Unit,
    onFirstChapter: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // "متابعة القراءة" Button
        Button(
            onClick = onContinueReading,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("continue_reading_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NexusPurple,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "متابعة القراءة (فصل $lastReadChapter)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // "الفصل الأول" Button
        OutlinedButton(
            onClick = onFirstChapter,
            modifier = Modifier
                .weight(0.7f)
                .height(48.dp)
                .testTag("first_chapter_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, NexusPurpleLight),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NexusPurpleLight
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "الفصل الأول",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

/**
 * Chapters List Header with batch indicator
 */
@Composable
fun ChaptersHeaderSection(
    totalChapters: Int,
    rangeText: String,
    currentBatch: Int,
    totalBatches: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "قائمة الفصول",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "يتم عرض 30 فصلاً في كل دفعة ($rangeText)",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NexusPurpleLight,
                    fontSize = 11.sp
                )
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = SurfaceVariantDark,
            border = BorderStroke(1.dp, SurfaceElevated)
        ) {
            Text(
                text = "دفعة $currentBatch من $totalBatches",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * Individual Chapter Item in the 30-chapter list
 */
@Composable
fun ChapterListItem(
    chapter: Chapter,
    isRead: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("details_chapter_${chapter.number}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) SurfaceDark else SurfaceCard
        ),
        border = BorderStroke(0.7.dp, if (isRead) SurfaceElevated.copy(alpha = 0.5f) else SurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRead) SurfaceElevated else NexusPurpleDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${chapter.number}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isRead) TextSecondary else TextPrimary
                        )
                    )
                }

                Column {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                            color = if (isRead) TextSecondary else TextPrimary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chapter.releaseDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (chapter.isNew) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BadgeNew
                    ) {
                        Text(
                            text = "NEW",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                if (isRead) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "تمت القراءة",
                        tint = BadgeSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Pagination Controls at the bottom:
 * - Switcher button taking the user to previous/next 30 chapters
 * - Direct batch chips for jumping between batches
 * - Return to Home button
 */
@Composable
fun BatchPaginationControl(
    currentBatchIndex: Int,
    totalBatches: Int,
    batchSize: Int,
    totalChapters: Int,
    onSelectBatch: (Int) -> Unit,
    onPreviousBatch: () -> Unit,
    onNextBatch: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "التبديل بين مجموعات الفصول (30 فصل)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            // Direct Batch Selection Chips: [1-30], [31-60], [61-90]
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(totalBatches) { batchIdx ->
                    val isSelected = batchIdx == currentBatchIndex
                    val startNum = batchIdx * batchSize + 1
                    val endNum = ((batchIdx + 1) * batchSize).coerceAtMost(totalChapters)
                    val label = "$startNum - $endNum"

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NexusPurple else SurfaceVariantDark,
                        border = BorderStroke(1.dp, if (isSelected) NexusPurpleLight else SurfaceElevated),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectBatch(batchIdx) }
                            .testTag("batch_chip_$batchIdx")
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Prev 30 & Next 30 Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "الـ 30 فصل السابقة"
                Button(
                    onClick = onPreviousBatch,
                    enabled = currentBatchIndex > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("prev_batch_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceVariantDark,
                        contentColor = TextPrimary,
                        disabledContainerColor = SurfaceDark,
                        disabledContentColor = TextTertiary
                    ),
                    border = BorderStroke(1.dp, SurfaceElevated)
                ) {
                    Text(
                        text = "◄ 30 فصل السابقة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // "الـ 30 فصل التالية"
                Button(
                    onClick = onNextBatch,
                    enabled = currentBatchIndex + 1 < totalBatches,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("next_batch_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusPurple,
                        contentColor = Color.White,
                        disabledContainerColor = SurfaceDark,
                        disabledContentColor = TextTertiary
                    )
                ) {
                    Text(
                        text = "30 فصل التالية ►",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = SurfaceElevated
            )

            // Return to Home Button (زر العودة للصفحة الرئيسية)
            OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("details_footer_home_button"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, NexusPurple.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NexusPurpleLight
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "العودة للصفحة الرئيسية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
