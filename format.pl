# 
# git diff --name-status > git_diff
# ./format.pl git_diff
use Cwd;
use File::Path;
use File::Copy;

$in = $ARGV[0];
$git_diff = $ARGV[0];

if( open(my $git_diff,'< $in') )
{
	while( $my_diffline = <$git_diff> )
	{
		$my_diffline =~ s/^[M A]\s//;
		
		if( $my_diffline =~ /.*\.java$/ )
		{
			chomp($my_diffline);
			$diff_out = $my_diffline . ".mod";
			printf "Diff file $diff_out \n";

			if( open(my $in_fh, "< $my_diffline") && open(my $out_fh, "> $diff_out") )
			{
				while( my $line = <$in_fh> )
				{
					my $copy = $line;
					$copy = s/\t/    /g;
					$copy = s/\s+$//;
					print $out_fh $copy;
					print $out_fh "\n";
				}
				close $in_fh;
				close $out_fh;
				move($diff_out,$my_diffline) || die "copy : $!";

			}

		}
		else
		{
			printf"Could not open $my_diffline \n";
		}

	}

}
